/**
 * CAPA: PRESENTACIÓN (Interface Adapters / UI)
 * --------------------------------------------------------------------------
 * Responsable EXCLUSIVO de pintar el DOM y capturar eventos del usuario.
 * Equivale a un Controller del backend, pero del lado del cliente.
 *
 * CLEAN ARCHITECTURE aplicada aquí:
 *  - SRP: solo renderiza y enlaza eventos. NO calcula reglas de negocio
 *    (eso es del dominio) ni sabe de dónde vienen los datos (eso es del puerto).
 *  - DIP: depende del `DashboardService` (caso de uso) inyectado, no de
 *    adaptadores concretos. Habla "hacia adentro", nunca con fetch/mock.
 *  - Regla del Controlador (README): recibe el evento, delega al caso de uso
 *    y refleja el resultado. Nunca procesa lógica de negocio.
 */

export class DashboardView {
  /**
   * @param {import("../application/DashboardService.js").DashboardService} service
   * @param {Document} doc
   */
  constructor(service, doc = document) {
    this.service = service;
    this.doc = doc;
  }

  async init() {
    this._bindToolbar(); // botones de Gmail / agregar (se enlazan una sola vez)
    await this.reload();
  }

  /** Carga datos del backend y repinta el tablero. Reutilizable tras cambios. */
  async reload() {
    this._setStatus("Cargando tablero…");
    try {
      const { upcomingPayments, analysis } = await this.service.loadDashboard(new Date());
      this._renderAnalysis(analysis);
      this._renderPayments(upcomingPayments);
      this._renderChart(upcomingPayments);
      this._setStatus("");
    } catch (err) {
      this._setStatus(`No se pudo cargar el tablero: ${err.message}`, true);
    }
  }

  /** Enlaza la barra de acciones: escanear Gmail y agregar suscripción. */
  _bindToolbar() {
    const scanBtn = this.doc.getElementById("scan-btn");
    if (scanBtn) scanBtn.addEventListener("click", () => this._onScan(scanBtn));

    const addForm = this.doc.getElementById("add-form");
    if (addForm) addForm.addEventListener("submit", (e) => this._onAdd(e));
  }

  async _onScan(button) {
    const original = button.textContent;
    button.disabled = true;
    button.textContent = "Escaneando…";
    this._setToolMsg("Conectando con tu correo y buscando suscripciones…");
    try {
      const result = await this.service.syncEmail();
      this._setToolMsg(result.message || "Escaneo terminado.", !result.success);
      if (result.success) await this.reload();
    } catch (err) {
      this._setToolMsg(`Error al escanear: ${err.message}`, true);
    } finally {
      button.disabled = false;
      button.textContent = original;
    }
  }

  async _onAdd(e) {
    e.preventDefault();
    const name = this.doc.getElementById("add-name").value.trim();
    const cost = this.doc.getElementById("add-cost").value;
    const cycle = this.doc.getElementById("add-cycle").value;
    const startDate = this.doc.getElementById("add-date").value;

    this._setToolMsg("Guardando suscripción…");
    try {
      const result = await this.service.add(name, Number(cost), cycle, startDate);
      this._setToolMsg(result.message || "Suscripción agregada.", !result.success);
      if (result.success) {
        e.target.reset();
        await this.reload();
      }
    } catch (err) {
      this._setToolMsg(`Error al agregar: ${err.message}`, true);
    }
  }

  async _onCancel(button) {
    const id = button.dataset.id;
    const name = button.dataset.name;
    if (!id) {
      this._setToolMsg(`No se puede cancelar "${name}": falta el identificador.`, true);
      return;
    }
    if (!confirm(`¿Cancelar la suscripción "${name}"? Se eliminará del tablero.`)) return;

    button.disabled = true;
    button.textContent = "Cancelando…";
    try {
      const result = await this.service.cancel(id);
      this._setToolMsg(result.message || "Suscripción cancelada.", !result.success);
      if (result.success) await this.reload();
      else { button.disabled = false; button.textContent = "Cancelar"; }
    } catch (err) {
      this._setToolMsg(`Error al cancelar: ${err.message}`, true);
      button.disabled = false;
      button.textContent = "Cancelar";
    }
  }

  _setToolMsg(text, isError = false) {
    const el = this.doc.getElementById("tool-msg");
    if (!el) return;
    el.textContent = text;
    el.classList.toggle("error", isError);
  }

  _renderAnalysis(a) {
    const el = this.doc.getElementById("analysis");
    el.innerHTML = `
      <div class="metric">
        <span class="metric-label">Gasto mensual total</span>
        <span class="metric-value">${this._money(a.totalMonthlyCost)}</span>
      </div>
      <div class="metric">
        <span class="metric-label">Suscripciones activas</span>
        <span class="metric-value">${a.subscriptionCount}</span>
      </div>
      <div class="metric">
        <span class="metric-label">Sugeridas para cancelar</span>
        <span class="metric-value warn">${a.cancelCount}</span>
      </div>
      <div class="metric">
        <span class="metric-label">Ahorro potencial / mes</span>
        <span class="metric-value good">${this._money(a.potentialMonthlySavings)}</span>
      </div>
    `;
  }

  _renderPayments(payments) {
    const list = this.doc.getElementById("payments");
    list.innerHTML = "";

    payments.forEach((p) => {
      const card = this.doc.createElement("article");
      card.className = "payment-card";
      const recoClass = p.recommendation === "CANCEL" ? "tag-cancel" : "tag-keep";
      const recoText = p.recommendation === "CANCEL" ? "Poco usada" : "Mantener";

      card.innerHTML = `
        <div class="payment-head">
          <h3>${p.name}</h3>
          <span class="tag ${recoClass}">${recoText}</span>
        </div>
        <p class="payment-meta">
          Próximo pago: <strong>${this._date(p.nextPaymentDate)}</strong>
          · ${p.billingCycle === "ANNUAL" ? "Anual" : "Mensual"}
          · Uso: ${p.daysUsedPerWeek} día(s)/sem
        </p>
        <div class="payment-foot">
          <span class="amount">${this._money(p.cost)}</span>
          <div class="card-actions">
            <button class="cancel-btn" data-id="${p.id ?? ""}" data-name="${p.name}">Cancelar</button>
            <button class="pay-btn" data-name="${p.name}">Pagar ahora</button>
          </div>
        </div>
        <p class="pay-feedback" data-feedback="${p.name}"></p>
      `;

      card.querySelector(".pay-btn").addEventListener("click", (e) =>
        this._onPay(e.currentTarget)
      );
      card.querySelector(".cancel-btn").addEventListener("click", (e) =>
        this._onCancel(e.currentTarget)
      );
      list.appendChild(card);
    });
  }

  /**
   * Gráfica de barras (SVG puro, sin librerías) del gasto mensual por
   * suscripción. SRP: solo dibuja; los datos ya vienen calculados por el
   * dominio (monthlyCost) y entregados por el caso de uso.
   */
  _renderChart(payments) {
    const el = this.doc.getElementById("chart");
    const data = payments.map((p) => ({ name: p.name, value: p.monthlyCost }));

    if (data.length === 0) {
      el.innerHTML = "<p class='status'>Sin datos para graficar.</p>";
      return;
    }

    const max = Math.max(...data.map((d) => d.value), 1);
    const barW = 56;
    const gap = 28;
    const chartH = 150;
    const topPad = 26;
    const bottomPad = 36;
    const sidePad = 18;
    const width = sidePad * 2 + data.length * barW + (data.length - 1) * gap;
    const height = topPad + chartH + bottomPad;

    const bars = data
      .map((d, i) => {
        const h = Math.round((d.value / max) * chartH);
        const x = sidePad + i * (barW + gap);
        const y = topPad + (chartH - h);
        const cx = x + barW / 2;
        return `
          <rect class="bar" x="${x}" y="${y}" width="${barW}" height="${h}" rx="6"></rect>
          <text class="bar-value" x="${cx}" y="${y - 8}" text-anchor="middle">${this._money(d.value)}</text>
          <text class="bar-label" x="${cx}" y="${topPad + chartH + 20}" text-anchor="middle">${d.name}</text>
        `;
      })
      .join("");

    // Tamaño FIJO en píxeles (width/height del propio SVG = al viewBox).
    // Así la gráfica se dibuja a escala 1:1 y nunca se agranda, sin depender
    // de CSS ni de porcentajes.
    el.innerHTML = `
      <svg class="bar-chart" width="${width}" height="${height}"
           viewBox="0 0 ${width} ${height}" role="img"
           aria-label="Gasto mensual por suscripción">
        <line class="axis" x1="${sidePad}" y1="${topPad + chartH}"
              x2="${width - sidePad}" y2="${topPad + chartH}"></line>
        ${bars}
      </svg>
    `;
  }

  async _onPay(button) {
    const name = button.dataset.name;
    const feedback = this.doc.querySelector(`[data-feedback="${name}"]`);
    button.disabled = true;
    button.textContent = "Procesando…";
    feedback.textContent = "";
    feedback.className = "pay-feedback";

    try {
      const result = await this.service.pay(name);
      feedback.textContent = result.message;
      feedback.classList.add(result.success ? "ok" : "error");
      button.textContent = result.success ? "Pagado" : "Reintentar";
      button.disabled = result.success;
    } catch (err) {
      feedback.textContent = `Error: ${err.message}`;
      feedback.classList.add("error");
      button.textContent = "Reintentar";
      button.disabled = false;
    }
  }

  _setStatus(text, isError = false) {
    const el = this.doc.getElementById("status");
    el.textContent = text;
    el.classList.toggle("error", isError);
  }

  _money(n) {
    return new Intl.NumberFormat("es-MX", { style: "currency", currency: "MXN" }).format(n);
  }

  _date(d) {
    return new Intl.DateTimeFormat("es-MX", { day: "2-digit", month: "short", year: "numeric" }).format(d);
  }
}
