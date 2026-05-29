package com.subscrybe.application.usecases;

import com.subscrybe.application.ports.out.ISubscriptionRepository;

public class DeleteSubscriptionUseCase {
    private final ISubscriptionRepository subscriptionRepository;

    public DeleteSubscriptionUseCase(ISubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public void execute(Long subscriptionId) {
        subscriptionRepository.deleteById(subscriptionId);
    }
}