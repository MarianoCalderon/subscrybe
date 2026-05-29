import { ISubscriptionGateway } from "../application/ISubscriptionGateway.js?v=7";
import { Subscription, Cycle } from "../domain/Subscription.js?v=7";

export class HttpSubscriptionGateway extends ISubscriptionGateway {
  constructor(baseUrl = "http://localhost:8081", userEmail = "irabien11@gmail.com") {
    super();
    this.baseUrl = baseUrl;
    this.userEmail = userEmail;
  }

  async getSubscriptions() {
    const url = `${this.baseUrl}/api/subscriptions?email=${encodeURIComponent(this.userEmail)}`;
    const res = await fetch(url);

    if (res.status === 204) {
      return [];
    }
    if (!res.ok) {
      throw new Error(`Error ${res.status} al obtener suscripciones`);
    }

    const data = await res.json();
    return data.map(
      (r) =>
        new Subscription(
          r.name,
          r.cost,
          r.billingCycle === "ANNUAL" ? Cycle.ANNUAL : Cycle.MONTHLY,
          new Date(r.startDate + "T00:00:00"),
          r.daysUsedPerWeek ?? 0,
          r.id ?? null
        )
    );
  }

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

  async deleteSubscription(id) {
    const res = await fetch(`${this.baseUrl}/api/subscriptions/${id}`, {
      method: "DELETE",
    });
    const message = await res.text();
    return { success: res.ok, message };
  }

  async addSubscription(name, cost, cycle, startDate) {
    const params = new URLSearchParams({ name, cost, cycle, startDate });
    const res = await fetch(`${this.baseUrl}/api/subscriptions/add?${params}`, {
      method: "POST",
    });
    const message = await res.text();
    return { success: res.ok, message };
  }

  async syncFromEmail() {
    const params = new URLSearchParams({ email: this.userEmail });
    const res = await fetch(`${this.baseUrl}/api/emails/sync?${params}`, {
      method: "POST",
    });
    const message = await res.text();
    return { success: res.ok, message };
  }
}
