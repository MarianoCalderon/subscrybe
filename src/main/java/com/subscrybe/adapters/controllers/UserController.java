package com.subscrybe.adapters.controllers;

import com.subscrybe.application.usecases.RegisterUserUseCase;
import com.subscrybe.domain.entities.User;
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
    public ResponseEntity<String> register(@RequestParam String name, @RequestParam String email) {
        try {
            User newUser = registerUserUseCase.execute(name, email);
            return ResponseEntity.status(201).body("Usuario " + newUser.getName() + " registrado exitosamente.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error en el servidor");
        }
    }
}