package com.subscrybe.adapters.controllers;

import com.subscrybe.application.usecases.RegisterUserUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final RegisterUserUseCase registerUserUseCase;

    // Spring inyectará automáticamente el Bean que configuramos en UseCaseConfig
    public UserController(RegisterUserUseCase registerUserUseCase) {
        this.registerUserUseCase = registerUserUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password) { // 1. Recibimos la contraseña desde la petición HTTP
        try {
            // 2. Pasamos los 3 parámetros. Como ahora es void, ya no lo asignamos a una variable
            registerUserUseCase.execute(name, email, password);

            return ResponseEntity.status(201).body("Usuario " + name + " registrado exitosamente.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error en el servidor");
        }
    }
}