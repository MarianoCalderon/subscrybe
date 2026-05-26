package com.subscrybe.application.usecases;

import com.subscrybe.application.ports.out.IPaymentGateway;
import com.subscrybe.application.ports.out.ISubscriptionRepository;
import com.subscrybe.domain.entities.Subscription;

public class ProcessPaymentUseCase {

    private final IPaymentGateway paymentGateway;
    private final ISubscriptionRepository subscriptionRepository;

    public ProcessPaymentUseCase(IPaymentGateway paymentGateway, ISubscriptionRepository subscriptionRepository) {
        this.paymentGateway = paymentGateway;
        this.subscriptionRepository = subscriptionRepository;
    }

    public boolean execute(String userEmail, String subscriptionName) {
        // 1. Buscamos la suscripción para saber cuánto cuesta
        Subscription subscription = subscriptionRepository.findByName(subscriptionName);

        if (subscription == null) {
            throw new IllegalArgumentException("Suscripción no encontrada");
        }

        // 2. Intentamos realizar el cobro a través de la pasarela
        String description = "Pago de suscripción: " + subscription.getName();
        boolean paymentSuccessful = paymentGateway.charge(userEmail, subscription.getCost(), description);

        // 3. Si el pago es exitoso, aquí podríamos actualizar la fecha del próximo pago
        // y hacer un repository.save(subscription). Por ahora, solo retornamos el estado.
        if (paymentSuccessful) {
            // Lógica futura: subscription.updateNextPaymentDate();
            // subscriptionRepository.save(subscription);
            return true;
        }

        return false; // El pago fue rechazado
    }
}