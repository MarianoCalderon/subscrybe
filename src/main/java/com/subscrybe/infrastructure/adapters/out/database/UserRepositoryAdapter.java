package com.subscrybe.infrastructure.adapters.out.database;

import com.subscrybe.application.ports.out.IUserRepository;
import com.subscrybe.domain.entities.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // Le dice a Spring que registre este adaptador automáticamente
public class UserRepositoryAdapter implements IUserRepository {

    private final SpringDataUserRepository jpaRepository;

    public UserRepositoryAdapter(SpringDataUserRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(User user) {
        // Traducimos el User del Dominio al UserJpaEntity de la base de datos
        UserJpaEntity entity = new UserJpaEntity(user.getName(), user.getEmail());
        jpaRepository.save(entity);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public User findByEmail(String email) {
        Optional<UserJpaEntity> entityOptional = jpaRepository.findByEmail(email);
        if (entityOptional.isPresent()) {
            UserJpaEntity entity = entityOptional.get();
            // Traducimos de vuelta al Dominio
            return new User(entity.getName(), entity.getEmail());
        }
        return null;
    }
}