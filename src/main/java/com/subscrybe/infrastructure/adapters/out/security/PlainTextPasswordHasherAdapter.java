package com.subscrybe.infrastructure.adapters.out.security;

import com.subscrybe.application.ports.out.IPasswordHasher;
import org.springframework.stereotype.Component;

@Component
public class PlainTextPasswordHasherAdapter implements IPasswordHasher {
    @Override
    public String hash(String rawPassword) {
        // Temporalmente devolvemos la misma contraseña.
        // Más adelante tu equipo de Seguridad cambiará esto por BCrypt.
        return rawPassword;
    }

    @Override
    public boolean matches(String rawPassword, String hashedPassword) {
        return rawPassword.equals(hashedPassword);
    }
}