/**
 * CAPA: APLICACIÓN (Puerto de salida / Output Port)
 * --------------------------------------------------------------------------
 * Equivale a `application/ports/out/ISubscriptionRepository.java` +
 * `IPaymentGateway.java` del backend.
 *
 * CLEAN ARCHITECTURE aplicada aquí:
 *  - DIP (Inversión de Dependencias): el caso de uso depende de ESTA
 *    abstracción, no de un detalle concreto (mock ni HTTP). La flecha de
 *    dependencia se invierte: la infraestructura implementa este contrato.
 *  - DEFINICIÓN DEL PUERTO EN EL LADO DEL CLIENTE: el contrato vive en la capa
 *    de aplicación (quien lo necesita), no en la de infraestructura.
 *  - ISP (Segregación de Interfaces): expone solo los métodos que el tablero
 *    realmente consume (listar y pagar). Nada más.
 *  - LSP (Sustitución de Liskov): cualquier subclase es intercambiable. JS no
 *    tiene interfaces, así que usamos una clase base abstracta cuyos métodos
 *    lanzan error si no se implementan, forzando el cumplimiento del contrato.
 *
 *  ⚠️ PUNTO DE CONEXIÓN A LA API REAL: este es el contrato que el backend debe
 *     satisfacer. Hoy lo cumple un adaptador Mock; mañana, el adaptador HTTP.
 */

export class ISubscriptionGateway {
  /**
   * Devuelve todas las suscripciones del usuario (para "próximos pagos").
   * @returns {Promise<import("../domain/Subscription.js").Subscription[]>}
   */
  async getSubscriptions() {
    throw new Error("ISubscriptionGateway.getSubscriptions() no implementado");
  }

  /**
   * Procesa el cobro de una suscripción (botón de pago).
   * @param {string} subscriptionName
   * @returns {Promise<{success: boolean, message: string}>}
   */
  async processPayment(subscriptionName) {
    throw new Error("ISubscriptionGateway.processPayment() no implementado");
  }

  /**
   * Elimina/cancela una suscripción por su id.
   * @param {number} id
   * @returns {Promise<{success: boolean, message: string}>}
   */
  async deleteSubscription(id) {
    throw new Error("ISubscriptionGateway.deleteSubscription() no implementado");
  }

  /**
   * Agrega una suscripción manualmente.
   * @returns {Promise<{success: boolean, message: string}>}
   */
  async addSubscription(name, cost, cycle, startDate) {
    throw new Error("ISubscriptionGateway.addSubscription() no implementado");
  }

  /**
   * Dispara el escaneo del correo (Gmail) para detectar suscripciones.
   * @returns {Promise<{success: boolean, message: string}>}
   */
  async syncFromEmail() {
    throw new Error("ISubscriptionGateway.syncFromEmail() no implementado");
  }
}
