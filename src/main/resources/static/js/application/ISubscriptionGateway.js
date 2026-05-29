export class ISubscriptionGateway {
  async getSubscriptions() {
    throw new Error("ISubscriptionGateway.getSubscriptions() no implementado");
  }

  async processPayment(subscriptionName) {
    throw new Error("ISubscriptionGateway.processPayment() no implementado");
  }

  async deleteSubscription(id) {
    throw new Error("ISubscriptionGateway.deleteSubscription() no implementado");
  }

  async addSubscription(name, cost, cycle, startDate) {
    throw new Error("ISubscriptionGateway.addSubscription() no implementado");
  }

  async syncFromEmail() {
    throw new Error("ISubscriptionGateway.syncFromEmail() no implementado");
  }
}
