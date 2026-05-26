package com.subscrybe.infrastructure.adapters.out.database;

import com.subscrybe.application.ports.out.ISubscriptionRepository;
import com.subscrybe.domain.entities.Cycle;
import com.subscrybe.domain.entities.Subscription;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class SubscriptionRepositoryAdapter implements ISubscriptionRepository {

    private final SpringDataSubscriptionRepository jpaRepository;

    public SubscriptionRepositoryAdapter(SpringDataSubscriptionRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(Subscription subscription) {
        // Traducimos del Dominio a la BD
        SubscriptionJpaEntity entity = new SubscriptionJpaEntity(
                subscription.getName(),
                subscription.getCost(),
                subscription.getBillingCycle().name(), // Pasamos el Enum a String
                subscription.getStartDate()
        );
        jpaRepository.save(entity);
    }

    @Override
    public Subscription findByName(String name) {
        Optional<SubscriptionJpaEntity> entityOpt = jpaRepository.findByName(name);

        if (entityOpt.isPresent()) {
            SubscriptionJpaEntity entity = entityOpt.get();
            // Traducimos de la BD de vuelta al Dominio
            return new Subscription(
                    entity.getName(),
                    entity.getCost(),
                    Cycle.valueOf(entity.getBillingCycle()), // Recuperamos el Enum
                    entity.getStartDate()
            );
        }
        return null; // Si no existe, retornamos null
    }


}