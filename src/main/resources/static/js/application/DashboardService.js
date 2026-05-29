/**
 * CAPA: APLICACIÓN (Caso de Uso / Application Business Rules)
 * --------------------------------------------------------------------------
 * Orquestador del tablero. Equivale a un UseCase del backend (p. ej.
 * ProcessPaymentUseCase): coordina el flujo sin contener reglas de negocio
 * de dominio ni detalles de UI/transporte.
 *
 * CLEAN ARCHITECTURE aplicada aquí:
 *  - SRP: su única responsabilidad es ORQUESTAR (pedir datos al puerto,
 *    delegar cálculos al dominio y entregar un modelo de vista listo).
 *  - DIP: recibe el puerto y el servicio de dominio POR CONSTRUCTOR
 *    (inyección de dependencias). No los crea, no sabe cuál implementación es.
 *  - OCP (Abierto/Cerrado): se le puede cambiar la fuente de datos (mock→HTTP)
 *    sin tocar una sola línea de esta clase, porque depende de la abstracción.
 *  - Separación presentación/lógica: NO toca el DOM. Devuelve datos puros que
 *    la capa de presentación renderiza.
 */

export class DashboardService {
  /**
   * @param {import("./ISubscriptionGateway.js").ISubscriptionGateway} gateway  Puerto inyectado.
   * @param {import("../domain/FinancialAnalyzer.js").FinancialAnalyzer} analyzer Servicio de dominio inyectado.
   */
  constructor(gateway, analyzer) {
    this.gateway = gateway;
    this.analyzer = analyzer;
  }

  /**
   * Construye el modelo de vista del tablero: próximos pagos ordenados +
   * análisis financiero. Delega el transporte al puerto y los cálculos al dominio.
   * @param {Date} [today=new Date()]
   */
  async loadDashboard(today = new Date()) {
    const subscriptions = await this.gateway.getSubscriptions();

    const upcomingPayments = subscriptions
      .map((sub) => ({
        name: sub.name,
        cost: sub.cost,
        monthlyCost: sub.monthlyCost(),
        billingCycle: sub.billingCycle,
        nextPaymentDate: sub.calculateNextPaymentDate(today),
        recommendation: this.analyzer.evaluateSubscription(sub),
        daysUsedPerWeek: sub.daysUsedPerWeek,
      }))
      .sort((a, b) => a.nextPaymentDate - b.nextPaymentDate);

    const analysis = {
      totalMonthlyCost: this.analyzer.totalMonthlyCost(subscriptions),
      potentialMonthlySavings: this.analyzer.potentialMonthlySavings(subscriptions),
      subscriptionCount: subscriptions.length,
      cancelCount: upcomingPayments.filter((p) => p.recommendation === "CANCEL").length,
    };

    return { upcomingPayments, analysis };
  }

  /**
   * Delega el cobro al puerto (botón de pago).
   * @param {string} subscriptionName
   */
  async pay(subscriptionName) {
    return this.gateway.processPayment(subscriptionName);
  }
}
