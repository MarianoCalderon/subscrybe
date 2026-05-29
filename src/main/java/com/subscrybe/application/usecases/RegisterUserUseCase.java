package com.subscrybe.application.usecases;

import com.subscrybe.application.ports.out.IPasswordHasher;
import com.subscrybe.application.ports.out.IUserRepository;
import com.subscrybe.domain.entities.User;

public class RegisterUserUseCase {

    private final IUserRepository userRepository;
    private final IPasswordHasher passwordHasher;

    public RegisterUserUseCase(IUserRepository userRepository, IPasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    public void execute(String name, String email, String rawPassword) {
        if (userRepository.findByEmail(email) != null) {
            throw new IllegalArgumentException("El correo ya está registrado.");
        }

        // Encriptamos la contraseña antes de crear la entidad
        String hashedPassword = passwordHasher.hash(rawPassword);

        // Usamos el nuevo constructor
        User newUser = new User(name, email, hashedPassword);

        userRepository.save(newUser);
    }
}