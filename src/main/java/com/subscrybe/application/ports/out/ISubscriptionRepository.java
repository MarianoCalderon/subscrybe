package com.subscrybe.application.ports.out;

import com.subscrybe.domain.entities.Subscription;

public interface ISubscriptionRepository {
    void save(Subscription subscription);
    Subscription findByName(String name);
}