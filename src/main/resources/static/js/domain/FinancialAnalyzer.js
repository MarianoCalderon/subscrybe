/**
 * CAPA: DOMINIO (Servicio de Dominio)
 * --------------------------------------------------------------------------
 * Espeja `domain/services/FinancialAnalyzer.java` del backend.
 *
 * CLEAN ARCHITECTURE aplicada aquí:
 *  - Servicio de Dominio: alberga una regla de negocio que no pertenece de
 *    forma natural a una sola entidad (la recomendación combina uso + costo).
 *  - SRP: su única razón de cambio es la política de recomendación financiera.
 *  - Pureza / Testabilidad: funciones sin estado ni efectos secundarios; se
 *    pueden probar en aislamiento total, igual que en el backend con TDD.
 */

export const Recommendation = Object.freeze({
  KEEP: "KEEP",
  CANCEL: "CANCEL",
});

export class FinancialAnalyzer {
  /**
   * Regla de negocio pura (idéntica al backend): si se usa menos de 2 días por
   * semana, se recomienda cancelar; en caso contrario, mantener.
   * @param {import("./Subscription.js").Subscription} subscription
   * @returns {"KEEP"|"CANCEL"}
   */
  evaluateSubscription(subscription) {
    if (subscription.daysUsedPerWeek < 2) {
      return Recommendation.CANCEL;
    }
    return Recommendation.KEEP;
  }

  /**
   * Gasto mensual total de una cartera de suscripciones.
   * Espeja `User.getTotalMonthlyCost` del backend.
   * @param {import("./Subscription.js").Subscription[]} subscriptions
   * @returns {number}
   */
  totalMonthlyCost(subscriptions) {
    return subscriptions.reduce((total, sub) => total + sub.monthlyCost(), 0);
  }

  /**
   * Ahorro potencial mensual si se cancelan las suscripciones marcadas CANCEL.
   * @param {import("./Subscription.js").Subscription[]} subscriptions
   * @returns {number}
   */
  potentialMonthlySavings(subscriptions) {
    return subscriptions
      .filter((sub) => this.evaluateSubscription(sub) === Recommendation.CANCEL)
      .reduce((total, sub) => total + sub.monthlyCost(), 0);
  }
}
