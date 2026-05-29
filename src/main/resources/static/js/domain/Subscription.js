/**
 * CAPA: DOMINIO (núcleo / Enterprise Business Rules)
 * --------------------------------------------------------------------------
 * Entidad rica de negocio. Espeja `domain/entities/Subscription.java` del backend.
 *
 * CLEAN ARCHITECTURE aplicada aquí:
 *  - Regla de la Dependencia: este módulo NO importa nada de aplicación,
 *    infraestructura ni del navegador (DOM, fetch). Es JavaScript puro.
 *    Las dependencias apuntan SIEMPRE hacia adentro: nadie de afuera lo
 *    contamina, él no conoce a nadie de afuera.
 *  - SRP (Responsabilidad Única): solo modela una suscripción y su única
 *    regla de negocio asociada (calcular su próxima fecha de pago).
 *  - Entidad rica (DDD): el comportamiento vive junto a los datos, no en
 *    "helpers" externos anémicos.
 */

export const Cycle = Object.freeze({
  MONTHLY: "MONTHLY",
  ANNUAL: "ANNUAL",
});

export class Subscription {
  /**
   * @param {string} name
   * @param {number} cost
   * @param {"MONTHLY"|"ANNUAL"} billingCycle
   * @param {Date} startDate
   * @param {number} daysUsedPerWeek  Días de uso semanal (insumo del análisis financiero).
   * @param {number|null} id  Identificador en el backend (necesario para cancelar/eliminar).
   */
  constructor(name, cost, billingCycle, startDate, daysUsedPerWeek = 0, id = null) {
    this.name = name;
    this.cost = cost;
    this.billingCycle = billingCycle;
    this.startDate = startDate;
    this.daysUsedPerWeek = daysUsedPerWeek;
    this.id = id;
  }

  /**
   * Regla de negocio pura: calcula la próxima fecha de cobro a partir de una
   * fecha de referencia. Espeja `Subscription.calculateNextPaymentDate` del
   * backend: avanza el ciclo hasta superar la fecha actual.
   * @param {Date} currentDate
   * @returns {Date}
   */
  calculateNextPaymentDate(currentDate) {
    const next = new Date(this.startDate.getTime());

    if (this.billingCycle === Cycle.ANNUAL) {
      while (next <= currentDate) {
        next.setFullYear(next.getFullYear() + 1);
      }
      return next;
    }

    // Por defecto / MONTHLY
    while (next <= currentDate) {
      next.setMonth(next.getMonth() + 1);
    }
    return next;
  }

  /**
   * Costo normalizado a base mensual, para que el análisis financiero compare
   * peras con peras (una anual se reparte en 12 meses).
   * @returns {number}
   */
  monthlyCost() {
    return this.billingCycle === Cycle.ANNUAL ? this.cost / 12 : this.cost;
  }
}
