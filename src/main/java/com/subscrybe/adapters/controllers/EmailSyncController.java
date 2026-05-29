package com.subscrybe.adapters.controllers;

import com.subscrybe.application.usecases.SyncSubscriptionsFromEmailUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/emails")
public class EmailSyncController {

    private final SyncSubscriptionsFromEmailUseCase syncUseCase;

    public EmailSyncController(SyncSubscriptionsFromEmailUseCase syncUseCase) {
        this.syncUseCase = syncUseCase;
    }

    @PostMapping("/sync")
    public ResponseEntity<String> syncFromEmail(@RequestParam String email) {
        try {
            syncUseCase.execute(email);
            return ResponseEntity.ok("Bandeja escaneada. Suscripciones guardadas en la base de datos.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al escanear el correo");
        }
    }
}