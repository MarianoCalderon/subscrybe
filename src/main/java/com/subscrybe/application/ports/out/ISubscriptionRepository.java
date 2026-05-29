package com.subscrybe.application.ports.out;

import com.subscrybe.domain.entities.Subscription;
import java.util.List; // ¡No olvides importar List!

public interface ISubscriptionRepository {
    void save(Subscription subscription);
    Subscription findByName(String name);

    // Agregamos los nuevos métodos que exigen nuestros Casos de Uso
    List<Subscription> findByUserEmail(String email);
    void deleteById(Long id);
}