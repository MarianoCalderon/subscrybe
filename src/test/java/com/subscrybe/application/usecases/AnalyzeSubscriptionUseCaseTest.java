package com.subscrybe.application.usecases;

import com.subscrybe.application.ports.out.ISubscriptionRepository;
import com.subscrybe.domain.entities.AnalysisResult;
import com.subscrybe.domain.entities.Cycle;
import com.subscrybe.domain.entities.Subscription;
import com.subscrybe.domain.services.FinancialAnalyzer;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class AnalyzeSubscriptionUseCaseTest {

    // Fake repository que simula tener una suscripción guardada (Dependency Inversion)
    class FakeSubscriptionRepository implements ISubscriptionRepository {
        private final Subscription stored;

        FakeSubscriptionRepository(Subscription stored) {
            this.stored = stored;
        }

        @Override
        public void save(Subscription subscription) {}

        @Override
        public Subscription findByName(String name) {
            return stored != null && stored.getName().equals(name) ? stored : null;
        }
        @Override
        public java.util.List<Subscription> findByUserEmail(String email) {
            return java.util.List.of();
        }
        @Override
        public void deleteById(Long id) {}
    }

    @Test
    void shouldReturnCancelForLowUsage() {
        Subscription netflix = new Subscription("Netflix", 299.0, Cycle.MONTHLY, LocalDate.now());
        AnalyzeSubscriptionUseCase useCase = new AnalyzeSubscriptionUseCase(
                new FakeSubscriptionRepository(netflix),
                new FinancialAnalyzer()
        );

        AnalysisResult result = useCase.execute("Netflix", 1);

        assertEquals("CANCEL", result.getRecommendation());
    }

    @Test
    void shouldReturnKeepForHighUsageAndLowCostPerSession() {
        Subscription spotify = new Subscription("Spotify", 129.0, Cycle.MONTHLY, LocalDate.now());
        AnalyzeSubscriptionUseCase useCase = new AnalyzeSubscriptionUseCase(
                new FakeSubscriptionRepository(spotify),
                new FinancialAnalyzer()
        );

        AnalysisResult result = useCase.execute("Spotify", 6);

        assertEquals("KEEP", result.getRecommendation());
    }

    @Test
    void shouldThrowWhenSubscriptionNotFound() {
        AnalyzeSubscriptionUseCase useCase = new AnalyzeSubscriptionUseCase(
                new FakeSubscriptionRepository(null),
                new FinancialAnalyzer()
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute("ServicioInexistente", 3)
        );
        assertTrue(exception.getMessage().contains("ServicioInexistente"));
    }

    @Test
    void shouldThrowWhenDaysOutOfRange() {
        Subscription spotify = new Subscription("Spotify", 129.0, Cycle.MONTHLY, LocalDate.now());
        AnalyzeSubscriptionUseCase useCase = new AnalyzeSubscriptionUseCase(
                new FakeSubscriptionRepository(spotify),
                new FinancialAnalyzer()
        );

        assertThrows(IllegalArgumentException.class, () -> useCase.execute("Spotify", 8));
        assertThrows(IllegalArgumentException.class, () -> useCase.execute("Spotify", -1));
    }
}
