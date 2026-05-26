package com.subscrybe.domain.services;

import com.subscrybe.domain.entities.Cycle;
import com.subscrybe.domain.entities.Subscription;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class FinancialAnalyzerTest {

    @Test
    void shouldRecommendCancellationIfUsedLessThanTwoDaysAWeek() {
        // Arrange
        Subscription disneyPlus = new Subscription("Disney+", 179.0, Cycle.MONTHLY, LocalDate.now());
        FinancialAnalyzer analyzer = new FinancialAnalyzer();

        // Act: El usuario responde en el cuestionario que lo usa 1 día a la semana
        int daysUsedPerWeek = 1;
        String recommendation = analyzer.evaluateSubscription(disneyPlus, daysUsedPerWeek);

        // Assert
        assertEquals("CANCEL", recommendation, "Debería recomendar cancelar por bajo uso");
    }

    @Test
    void shouldRecommendKeepingIfUsedFrequently() {
        // Arrange
        Subscription spotify = new Subscription("Spotify", 129.0, Cycle.MONTHLY, LocalDate.now());
        FinancialAnalyzer analyzer = new FinancialAnalyzer();

        // Act: El usuario responde que lo usa 5 días a la semana
        int daysUsedPerWeek = 5;
        String recommendation = analyzer.evaluateSubscription(spotify, daysUsedPerWeek);

        // Assert
        assertEquals("KEEP", recommendation, "Debería recomendar mantener por uso frecuente");
    }
}