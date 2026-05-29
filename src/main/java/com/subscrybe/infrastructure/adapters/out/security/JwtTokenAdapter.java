package com.subscrybe.infrastructure.adapters.out.security;

import com.subscrybe.application.ports.out.ITokenGenerator;
import com.subscrybe.domain.entities.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenAdapter implements ITokenGenerator {

    // En un proyecto real, esta clave secreta debe ir en el application.properties
    // Usamos una generada aleatoriamente y súper segura para este ejemplo
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Tiempo de expiración: 24 horas (en milisegundos)
    private final long EXPIRATION_TIME = 86400000;

    @Override
    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .setSubject(user.getEmail()) // El "dueño" del token
                .claim("name", user.getName()) // Datos extra (opcional)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key) // Firmamos el token matemáticamente
                .compact();
    }
}