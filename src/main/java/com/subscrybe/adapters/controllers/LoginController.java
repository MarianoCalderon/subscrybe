package com.subscrybe.adapters.controllers;

import com.subscrybe.application.usecases.LoginUserUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    private final LoginUserUseCase loginUserUseCase;

    public LoginController(LoginUserUseCase loginUserUseCase) {
        this.loginUserUseCase = loginUserUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestParam String email,
            @RequestParam String password) {
        try {
            // Si el correo y contraseña son correctos, nos devuelve el JWT
            String token = loginUserUseCase.execute(email, password);
            return ResponseEntity.ok(token);
        } catch (IllegalArgumentException e) {
            // Si hay error en las credenciales (Excepción del Caso de Uso)
            return ResponseEntity.status(401).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error interno del servidor");
        }
    }
}