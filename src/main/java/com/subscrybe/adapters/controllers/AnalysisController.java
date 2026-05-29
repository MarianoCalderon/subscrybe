package com.subscrybe.adapters.controllers;

import com.subscrybe.application.usecases.AnalyzeSubscriptionUseCase;
import com.subscrybe.domain.entities.AnalysisResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analysis")
public class AnalysisController {

    private final AnalyzeSubscriptionUseCase useCase;

    public AnalysisController(AnalyzeSubscriptionUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/evaluate")
    public ResponseEntity<?> evaluate(
            @RequestParam String subscriptionName,
            @RequestParam int daysPerWeek) {
        try {
            AnalysisResult result = useCase.execute(subscriptionName, daysPerWeek);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error interno del servidor");
        }
    }
}
