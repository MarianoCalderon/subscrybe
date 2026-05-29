package com.subscrybe.infrastructure.adapters.out.database;

import com.subscrybe.application.ports.out.ISubscriptionRepository;
import com.subscrybe.domain.entities.Cycle;
import com.subscrybe.domain.entities.Subscription;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class SubscriptionRepositoryAdapter implements ISubscriptionRepository {

    private final SpringDataSubscriptionRepository jpaRepository;

    public SubscriptionRepositoryAdapter(SpringDataSubscriptionRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(Subscription subscription) {
        SubscriptionJpaEntity entity = new SubscriptionJpaEntity(
                subscription.getName(),
                subscription.getCost(),
                subscription.getBillingCycle().name(),
                subscription.getStartDate(),
                "marianocalderon82@gmail.com" // <-- 1. CORREGIDO AL CORREO REAL
        );
        jpaRepository.save(entity);
    }

    @Override
    public Subscription findByName(String name) {
        Optional<SubscriptionJpaEntity> entityOpt = jpaRepository.findByName(name);

        if (entityOpt.isPresent()) {
            SubscriptionJpaEntity entity = entityOpt.get();
            Subscription sub = new Subscription(
                    entity.getName(),
                    entity.getCost(),
                    Cycle.valueOf(entity.getBillingCycle()),
                    entity.getStartDate()
            );
            sub.setId(entity.getId()); // También agregamos el ID aquí por si acaso
            return sub;
        }
        return null;
    }

    @Override
    public List<Subscription> findByUserEmail(String email) {
        List<SubscriptionJpaEntity> entities = jpaRepository.findByUserEmail(email);

        return entities.stream()
                .map(entity -> {
                    Subscription sub = new Subscription(
                            entity.getName(),
                            entity.getCost(),
                            Cycle.valueOf(entity.getBillingCycle()),
                            entity.getStartDate()
                    );
                    // 👇 2. EL PASO CLAVE: Pasamos el ID de la BD a tu Dominio
                    sub.setId(entity.getId());
                    return sub;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}