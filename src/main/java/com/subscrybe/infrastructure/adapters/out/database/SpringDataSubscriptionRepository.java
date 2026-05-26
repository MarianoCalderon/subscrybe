package com.subscrybe.infrastructure.adapters.out.database;

import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface SpringDataSubscriptionRepository extends JpaRepository<SubscriptionJpaEntity, Long> {
    // Spring Boot genera el SQL automáticamente basándose en el nombre del método
    Optional<SubscriptionJpaEntity> findByName(String name);
}