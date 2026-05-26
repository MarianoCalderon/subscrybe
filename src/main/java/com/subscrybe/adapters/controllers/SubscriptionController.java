package com.subscrybe.adapters.controllers;

import com.subscrybe.application.usecases.AddSubscriptionUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final AddSubscriptionUseCase useCase;

    // Spring inyectará automáticamente el Caso de Uso aquí gracias a @RestController
    public SubscriptionController(AddSubscriptionUseCase useCase) {
        this.useCase = useCase;
    }

    // Transformamos el método en un endpoint POST real accesible por HTTP
    @PostMapping("/add")
    public ResponseEntity<String> addSubscription(
            @RequestParam String name,
            @RequestParam double cost,
            @RequestParam String cycle,
            @RequestParam String startDate) {
        try {
            // El controlador sigue cumpliendo su única responsabilidad:
            // recibir datos de la web y delegar la lógica pesada al Caso de Uso
            useCase.execute(name, cost, cycle, startDate);
            return ResponseEntity.ok("Suscripción '" + name + "' guardada con éxito.");
        } catch (IllegalArgumentException e) {
            // Si las reglas de negocio del dominio fallan, atrapamos el error con un 400 Bad Request
            return ResponseEntity.badRequest().body("Error de validación: " + e.getMessage());
        } catch (Exception e) {
            // Cualquier otro fallo inesperado devuelve un 500 Internal Server Error
            return ResponseEntity.status(500).body("Error interno del servidor");
        }
    }
}