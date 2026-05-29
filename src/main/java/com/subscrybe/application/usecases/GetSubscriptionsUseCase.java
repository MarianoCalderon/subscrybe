package com.subscrybe.application.usecases;

import com.subscrybe.application.ports.out.ISubscriptionRepository;
import com.subscrybe.domain.entities.Subscription;
import java.util.List;

public class GetSubscriptionsUseCase {
    private final ISubscriptionRepository subscriptionRepository;

    public GetSubscriptionsUseCase(ISubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public List<Subscription> execute(String userEmail) {
        // Asumiendo que agregas este método a tu ISubscriptionRepository
        return subscriptionRepository.findByUserEmail(userEmail);
    }
}