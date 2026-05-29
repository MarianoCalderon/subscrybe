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
        Subscription subscription = subscriptionRepository.findByName(subscriptionName);

        if (subscription == null) {
            throw new IllegalArgumentException("Suscripción no encontrada");
        }

        String description = "Pago de suscripción: " + subscription.getName();
        boolean paymentSuccessful = paymentGateway.charge(userEmail, subscription.getCost(), description);

        if (paymentSuccessful) {
            return true;
        }

        return false;
    }
}