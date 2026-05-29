/**
 * CAPA: INFRAESTRUCTURA (Adaptador de salida / Driven Adapter)
 * --------------------------------------------------------------------------
 * ⚠️ PUNTO DE CONEXIÓN A LA API REAL.
 *
 * Implementación REAL del puerto `ISubscriptionGateway` usando `fetch`. Hoy NO
 * está activa: el `main.js` (composition root) inyecta el Mock. Para pasar a
 * producción basta cambiar UNA línea en `main.js` (instanciar este adaptador en
 * vez del Mock). Esa es la recompensa de la Inversión de Dependencias: el núcleo
 * no cambia.
 *
 * Rutas REALES que expone el backend (verificadas contra los @RestController):
 *   GET  /api/subscriptions?email={email}      -> lista de suscripciones (JSON)
 *   POST /api/payments/process                 -> procesa el cobro
 *        ?userEmail={email}&subscriptionName={name}
 *
 * ⚠️ DESFASE CONOCIDO DE MODELO (documentado a propósito, no es un bug):
 *   El backend NO persiste `daysUsedPerWeek`. Ese dato es el insumo del análisis
 *   financiero (recomendación Cancelar/Mantener y ahorro potencial), pero el
 *   backend lo recibe aparte en `GET /api/analysis/evaluate?daysPerWeek=...` y no
 *   lo guarda. Por eso aquí lo dejamos en 0 por defecto: NO inventamos uso real.
 *   Mientras el backend no persista ese campo, el análisis del tablero será
 *   neutral al usar datos reales. El Mock sí lo trae, por eso es el default.
 *
 * CLEAN ARCHITECTURE aplicada aquí:
 *  - LSP: intercambiable con el Mock; el resto del sistema no se entera.
 *  - SRP: su única responsabilidad es hablar HTTP y traducir JSON→Dominio.
 *  - Frontier de la arquitectura: aquí (y solo aquí) viven `fetch`, rutas y
 *    formatos de transporte. El detalle técnico queda confinado al borde.
 */

import { ISubscriptionGateway } from "../application/ISubscriptionGateway.js?v=6";
import { Subscription, Cycle } from "../domain/Subscription.js?v=6";

export class HttpSubscriptionGateway extends ISubscriptionGateway {
  /**
   * @param {string} baseUrl   Origen del backend.
   * @param {string} userEmail Correo dueño de las suscripciones (el backend ata
   *                            cada suscripción a un email).
   */
  constructor(baseUrl = "http://localhost:8081", userEmail = "irabien11@gmail.com") {
    super();
    this.baseUrl = baseUrl;
    this.userEmail = userEmail;
  }

  /** @returns {Promise<Subscription[]>} */
  async getSubscriptions() {
    const url = `${this.baseUrl}/api/subscriptions?email=${encodeURIComponent(this.userEmail)}`;
    const res = await fetch(url);

    // El backend responde 204 (sin contenido) cuando el usuario no tiene
    // suscripciones; lo traducimos a lista vacía, no a error.
    if (res.status === 204) {
      return [];
    }
    if (!res.ok) {
      throw new Error(`Error ${res.status} al obtener suscripciones`);
    }

    const data = await res.json();
    // Traducción DTO (transporte) -> Entidad de Dominio.
    return data.map(
      (r) =>
        new Subscription(
          r.name,
          r.cost,
          r.billingCycle === "ANNUAL" ? Cycle.ANNUAL : Cycle.MONTHLY,
          new Date(r.startDate + "T00:00:00"),
          // Ver "DESFASE CONOCIDO" arriba: el backend no entrega este dato.
          r.daysUsedPerWeek ?? 0,
          r.id ?? null // id real del backend, necesario para cancelar
        )
    );
  }

  /** @returns {Promise<{success: boolean, message: string}>} */
  async processPayment(subscriptionName) {
    const params = new URLSearchParams({
      userEmail: this.userEmail,
      subscriptionName,
    });
    const res = await fetch(`${this.baseUrl}/api/payments/process?${params}`, {
      method: "POST",
    });
    const message = await res.text();
    return { success: res.ok, message };
  }

  /** Cancela/elimina una suscripción -> DELETE /api/subscriptions/{id} */
  async deleteSubscription(id) {
    const res = await fetch(`${this.baseUrl}/api/subscriptions/${id}`, {
      method: "DELETE",
    });
    const message = await res.text();
    return { success: res.ok, message };
  }

  /** Agrega una suscripción -> POST /api/subscriptions/add?name=&cost=&cycle=&startDate= */
  async addSubscription(name, cost, cycle, startDate) {
    const params = new URLSearchParams({ name, cost, cycle, startDate });
    const res = await fetch(`${this.baseUrl}/api/subscriptions/add?${params}`, {
      method: "POST",
    });
    const message = await res.text();
    return { success: res.ok, message };
  }

  /** Escanea el correo del usuario -> POST /api/emails/sync?email= */
  async syncFromEmail() {
    const params = new URLSearchParams({ email: this.userEmail });
    const res = await fetch(`${this.baseUrl}/api/emails/sync?${params}`, {
      method: "POST",
    });
    const message = await res.text();
    return { success: res.ok, message };
  }
}
