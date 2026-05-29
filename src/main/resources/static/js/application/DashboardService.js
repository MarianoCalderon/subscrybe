export class DashboardService {
  constructor(gateway, analyzer) {
    this.gateway = gateway;
    this.analyzer = analyzer;
  }

  async loadDashboard(today = new Date()) {
    const subscriptions = await this.gateway.getSubscriptions();

    const upcomingPayments = subscriptions
      .map((sub) => ({
        id: sub.id,
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

  async pay(subscriptionName) {
    return this.gateway.processPayment(subscriptionName);
  }

  async cancel(id) {
    return this.gateway.deleteSubscription(id);
  }

  async add(name, cost, cycle, startDate) {
    return this.gateway.addSubscription(name, cost, cycle, startDate);
  }

  async syncEmail() {
    return this.gateway.syncFromEmail();
  }
}
