package com.subscrybe.application.usecases;

import com.subscrybe.application.ports.out.IUserRepository;
import com.subscrybe.domain.entities.User;

public class RegisterUserUseCase {

    private final IUserRepository userRepository;

    public RegisterUserUseCase(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User execute(String name, String email) {
        // Regla de negocio: No pueden haber dos cuentas con el mismo correo
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("El correo ya está registrado.");
        }

        User newUser = new User(name, email);
        userRepository.save(newUser);

        return newUser;
    }
}