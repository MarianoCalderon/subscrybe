package com.subscrybe.adapters.controllers;

import com.subscrybe.application.usecases.AddSubscriptionUseCase;
import com.subscrybe.application.usecases.DeleteSubscriptionUseCase;
import com.subscrybe.application.usecases.GetSubscriptionsUseCase;
import com.subscrybe.domain.entities.Subscription;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@CrossOrigin(origins = "*") // ⚠️ Permite que el equipo de frontend se conecte sin bloqueos de seguridad
public class SubscriptionController {

    private final AddSubscriptionUseCase addUseCase;
    private final GetSubscriptionsUseCase getSubscriptionsUseCase;
    private final DeleteSubscriptionUseCase deleteSubscriptionUseCase;

    // Spring inyecta automáticamente los tres Casos de Uso
    public SubscriptionController(AddSubscriptionUseCase addUseCase,
                                  GetSubscriptionsUseCase getSubscriptionsUseCase,
                                  DeleteSubscriptionUseCase deleteSubscriptionUseCase) {
        this.addUseCase = addUseCase;
        this.getSubscriptionsUseCase = getSubscriptionsUseCase;
        this.deleteSubscriptionUseCase = deleteSubscriptionUseCase;
    }

    // 1. Endpoint para agregar manualmente
    @PostMapping("/add")
    public ResponseEntity<String> addSubscription(
            @RequestParam String name,
            @RequestParam double cost,
            @RequestParam String cycle,
            @RequestParam String startDate) {
        try {
            addUseCase.execute(name, cost, cycle, startDate);
            return ResponseEntity.ok("Suscripción '" + name + "' guardada con éxito.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error de validación: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error interno del servidor");
        }
    }

    // 2. Endpoint para ver el tablero
    @GetMapping
    public ResponseEntity<List<Subscription>> getUserSubscriptions(@RequestParam String email) {
        List<Subscription> subscriptions = getSubscriptionsUseCase.execute(email);

        if (subscriptions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(subscriptions);
    }

    // 3. Endpoint para eliminar
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSubscription(@PathVariable Long id) {
        try {
            deleteSubscriptionUseCase.execute(id);
            return ResponseEntity.ok("Suscripción eliminada con éxito.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al eliminar: " + e.getMessage());
        }
    }
}