export const Cycle = Object.freeze({
  MONTHLY: "MONTHLY",
  ANNUAL: "ANNUAL",
});

export class Subscription {
  constructor(name, cost, billingCycle, startDate, daysUsedPerWeek = 0, id = null) {
    this.name = name;
    this.cost = cost;
    this.billingCycle = billingCycle;
    this.startDate = startDate;
    this.daysUsedPerWeek = daysUsedPerWeek;
    this.id = id;
  }

  calculateNextPaymentDate(currentDate) {
    const next = new Date(this.startDate.getTime());

    if (this.billingCycle === Cycle.ANNUAL) {
      while (next <= currentDate) {
        next.setFullYear(next.getFullYear() + 1);
      }
      return next;
    }

    while (next <= currentDate) {
      next.setMonth(next.getMonth() + 1);
    }
    return next;
  }

  monthlyCost() {
    return this.billingCycle === Cycle.ANNUAL ? this.cost / 12 : this.cost;
  }
}
