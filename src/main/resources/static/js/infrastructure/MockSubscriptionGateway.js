/**
 * CAPA: INFRAESTRUCTURA (Adaptador de salida / Driven Adapter)
 * --------------------------------------------------------------------------
 * Implementación de PRUEBA del puerto `ISubscriptionGateway`, con datos
 * simulados en memoria. Equivale a los "Fake Repositories" que el backend usa
 * en sus pruebas unitarias (ver README, sección TDD).
 *
 * CLEAN ARCHITECTURE aplicada aquí:
 *  - LSP: cumple el contrato del puerto; es 100% sustituible por el adaptador
 *    HTTP sin que el caso de uso ni la vista lo noten.
 *  - DIP en acción: es un DETALLE. Vive en la capa más externa y puede
 *    desaparecer sin afectar al núcleo.
 *  - Traducción de datos: convierte datos crudos (lo que "vendría" de la API)
 *    en ENTIDADES de dominio, igual que un RepositoryAdapter mapea JPA→Dominio.
 */

import { ISubscriptionGateway } from "../application/ISubscriptionGateway.js";
import { Subscription, Cycle } from "../domain/Subscription.js";

const RAW_DATA = [
  { name: "Spotify", cost: 119.0, cycle: "MONTHLY", startDate: "2026-01-15", daysUsedPerWeek: 6 },
  { name: "Netflix", cost: 219.0, cycle: "MONTHLY", startDate: "2026-02-03", daysUsedPerWeek: 3 },
  { name: "Disney+", cost: 159.0, cycle: "MONTHLY", startDate: "2026-01-28", daysUsedPerWeek: 1 },
  { name: "Amazon Prime", cost: 999.0, cycle: "ANNUAL", startDate: "2026-03-10", daysUsedPerWeek: 4 },
  { name: "Adobe CC", cost: 599.0, cycle: "MONTHLY", startDate: "2026-02-20", daysUsedPerWeek: 0 },
];

export class MockSubscriptionGateway extends ISubscriptionGateway {
  /** @returns {Promise<Subscription[]>} */
  async getSubscriptions() {
    // Simulamos latencia de red para que la UI ejercite sus estados de carga.
    await this._delay(250);
    return RAW_DATA.map(
      (r) =>
        new Subscription(
          r.name,
          r.cost,
          r.cycle === "ANNUAL" ? Cycle.ANNUAL : Cycle.MONTHLY,
          new Date(r.startDate + "T00:00:00"),
          r.daysUsedPerWeek
        )
    );
  }

  /** @returns {Promise<{success: boolean, message: string}>} */
  async processPayment(subscriptionName) {
    await this._delay(600);
    const exists = RAW_DATA.some((r) => r.name === subscriptionName);
    if (!exists) {
      return { success: false, message: `Suscripción '${subscriptionName}' no encontrada` };
    }
    // El mock siempre aprueba el cobro; el adaptador real consultará la pasarela.
    return { success: true, message: `Pago de '${subscriptionName}' procesado con éxito` };
  }

  _delay(ms) {
    return new Promise((resolve) => setTimeout(resolve, ms));
  }
}
