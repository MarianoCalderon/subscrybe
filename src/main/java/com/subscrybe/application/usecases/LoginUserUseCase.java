package com.subscrybe.application.usecases;

import com.subscrybe.application.ports.out.IPasswordHasher;
import com.subscrybe.application.ports.out.ITokenGenerator;
import com.subscrybe.application.ports.out.IUserRepository;
import com.subscrybe.domain.entities.User;

public class LoginUserUseCase {

    private final IUserRepository userRepository;
    private final IPasswordHasher passwordHasher;
    private final ITokenGenerator tokenGenerator;

    public LoginUserUseCase(IUserRepository userRepository,
                            IPasswordHasher passwordHasher,
                            ITokenGenerator tokenGenerator) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.tokenGenerator = tokenGenerator;
    }

    public String execute(String email, String rawPassword) {
        // 1. Verificar si el usuario existe
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("Credenciales inválidas.");
        }

        // 2. Verificar que la contraseña coincida
        if (!passwordHasher.matches(rawPassword, user.getPassword())) {
            throw new IllegalArgumentException("Credenciales inválidas.");
        }

        // 3. Generar y retornar el token de acceso
        return tokenGenerator.generateToken(user);
    }
}