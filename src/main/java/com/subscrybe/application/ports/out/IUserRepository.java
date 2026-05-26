package com.subscrybe.application.ports.out;

import com.subscrybe.domain.entities.User;

public interface IUserRepository {
    void save(User user);

    boolean existsByEmail(String email);

    // ¡Agregamos el método que faltaba en el contrato!
    User findByEmail(String email);
}