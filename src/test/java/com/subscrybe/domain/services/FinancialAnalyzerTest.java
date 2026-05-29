package com.subscrybe.domain.services;

import com.subscrybe.domain.entities.AnalysisResult;
import com.subscrybe.domain.entities.Cycle;
import com.subscrybe.domain.entities.Subscription;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class FinancialAnalyzerTest {

    private final FinancialAnalyzer analyzer = new FinancialAnalyzer();

    @Test
    void shouldRecommendCancelWhenNotUsedAtAll() {
        Subscription disneyPlus = new Subscription("Disney+", 179.0, Cycle.MONTHLY, LocalDate.now());

        AnalysisResult result = analyzer.evaluateSubscription(disneyPlus, 0);

        assertEquals("CANCEL", result.getRecommendation(), "Sin uso = cancelar");
        assertEquals(0.0, result.getCostPerSession());
    }

    @Test
    void shouldRecommendCancelWhenUsedLessThanTwoDaysAWeek() {
        // Disney+ $179/mes, 1 día/semana → costo por sesión $41.3 pero igual CANCEL por uso bajo
        Subscription disneyPlus = new Subscription("Disney+", 179.0, Cycle.MONTHLY, LocalDate.now());

        AnalysisResult result = analyzer.evaluateSubscription(disneyPlus, 1);

        assertEquals("CANCEL", result.getRecommendation(), "Uso bajo (< 2 días) = cancelar");
    }

    @Test
    void shouldRecommendCancelWhenCostPerSessionIsTooHigh() {
        // $500/mes, 2 días/semana → costo por sesión ≈ $57.7 > $50 → CANCEL
        Subscription expensiveService = new Subscription("Premium", 500.0, Cycle.MONTHLY, LocalDate.now());

        AnalysisResult result = analyzer.evaluateSubscription(expensiveService, 2);

        assertEquals("CANCEL", result.getRecommendation(), "Costo por sesión > $50 = cancelar");
        assertTrue(result.getCostPerSession() > 50.0);
    }

    @Test
    void shouldRecommendReduceWhenCostPerSessionIsModerate() {
        // $299/mes, 2 días/semana → costo por sesión ≈ $34.5, entre $25 y $50 → REDUCE
        Subscription netflix = new Subscription("Netflix", 299.0, Cycle.MONTHLY, LocalDate.now());

        AnalysisResult result = analyzer.evaluateSubscription(netflix, 2);

        assertEquals("REDUCE", result.getRecommendation(), "Costo moderado = reducir plan");
        assertTrue(result.getCostPerSession() > 25.0 && result.getCostPerSession() <= 50.0);
    }

    @Test
    void shouldRecommendKeepWhenUsedFrequentlyAndCheapPerSession() {
        // Spotify $129/mes, 5 días/semana → costo por sesión ≈ $5.96 < $25 → KEEP
        Subscription spotify = new Subscription("Spotify", 129.0, Cycle.MONTHLY, LocalDate.now());

        AnalysisResult result = analyzer.evaluateSubscription(spotify, 5);

        assertEquals("KEEP", result.getRecommendation(), "Bajo costo por sesión y uso frecuente = mantener");
        assertTrue(result.getCostPerSession() < 25.0);
    }

    @Test
    void shouldNormalizeAnnualCostToMonthlyBeforeAnalysis() {
        // Plan anual $1200/año = $100/mes. 5 días/semana → $4.6/sesión → KEEP
        Subscription annualPlan = new Subscription("Servicio Anual", 1200.0, Cycle.ANNUAL, LocalDate.now());

        AnalysisResult result = analyzer.evaluateSubscription(annualPlan, 5);

        assertEquals("KEEP", result.getRecommendation(), "Costo anual normalizado correctamente");
        assertEquals(100.0, result.getMonthlyCost(), 0.01, "Costo mensual debe ser 1200/12");
    }
}