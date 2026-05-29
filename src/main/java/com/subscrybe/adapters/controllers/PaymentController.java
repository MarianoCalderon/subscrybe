package com.subscrybe.adapters.controllers;

import com.subscrybe.application.usecases.ProcessPaymentUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final ProcessPaymentUseCase processPaymentUseCase;

    public PaymentController(ProcessPaymentUseCase processPaymentUseCase) {
        this.processPaymentUseCase = processPaymentUseCase;
    }

    @PostMapping("/process")
    public ResponseEntity<String> processPayment(
            @RequestParam String userEmail,
            @RequestParam String subscriptionName) {
        try {
            // Controller delegating work to use case
            boolean isSuccessful = processPaymentUseCase.execute(userEmail, subscriptionName);

            if (isSuccessful) {
                return ResponseEntity.ok("Payment for '" + subscriptionName + "' was successfully processed.");
            } else {
                return ResponseEntity.badRequest().body("Payment was declined by the gateway.");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Validation error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An internal server error occurred.");
        }
    }
}