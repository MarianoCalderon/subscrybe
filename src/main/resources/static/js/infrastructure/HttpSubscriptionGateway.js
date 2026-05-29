/**
 * CAPA: INFRAESTRUCTURA (Adaptador de salida / Driven Adapter)
 * --------------------------------------------------------------------------
 * ⚠️ PUNTO DE CONEXIÓN A LA API REAL.
 *
 * Implementación REAL del puerto `ISubscriptionGateway` usando `fetch`.
 * Hoy NO está activa: el `main.js` (composition root) inyecta el Mock. Para
 * pasar a producción basta cambiar UNA línea en `main.js` (instanciar este
 * adaptador en vez del Mock). Esa es la recompensa de la Inversión de
 * Dependencias: el núcleo no cambia.
 *
 * Para que funcione, el backend deberá exponer (siguiendo el flujo Inside-Out
 * de 6 pasos del README):
 *   GET  /api/subscriptions          -> lista de suscripciones (JSON)
 *   POST /api/subscriptions/pay      -> procesa el cobro {subscriptionName}
 *
 * CLEAN ARCHITECTURE aplicada aquí:
 *  - LSP: intercambiable con el Mock; el resto del sistema no se entera.
 *  - SRP: su única responsabilidad es hablar HTTP y traducir JSON→Dominio.
 *  - Frontier de la arquitectura: aquí (y solo aquí) viven `fetch`, rutas y
 *    formatos de transporte. El detalle técnico queda confinado al borde.
 */

import { ISubscriptionGateway } from "../application/ISubscriptionGateway.js";
import { Subscription, Cycle } from "../domain/Subscription.js";

export class HttpSubscriptionGateway extends ISubscriptionGateway {
  /** @param {string} baseUrl */
  constructor(baseUrl = "http://localhost:8081") {
    super();
    this.baseUrl = baseUrl;
  }

  /** @returns {Promise<Subscription[]>} */
  async getSubscriptions() {
    const res = await fetch(`${this.baseUrl}/api/subscriptions`);
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
          r.daysUsedPerWeek ?? 0
        )
    );
  }

  /** @returns {Promise<{success: boolean, message: string}>} */
  async processPayment(subscriptionName) {
    const res = await fetch(`${this.baseUrl}/api/subscriptions/pay`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ subscriptionName }),
    });
    const message = await res.text();
    return { success: res.ok, message };
  }
}
