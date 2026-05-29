package com.subscrybe.application.usecases;

import com.subscrybe.application.ports.out.IPaymentGateway;
import com.subscrybe.application.ports.out.ISubscriptionRepository;
import com.subscrybe.domain.entities.Cycle;
import com.subscrybe.domain.entities.Subscription;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProcessPaymentUseCaseTest {

    private static class FakeSubscriptionRepository implements ISubscriptionRepository {

        private Subscription subscription;

        private FakeSubscriptionRepository(Subscription subscription) {
            this.subscription = subscription;
        }

        @Override
        public void save(Subscription subscription) {
            this.subscription = subscription;
        }

        @Override
        public Subscription findByName(String name) {
            return subscription;
        }

        @Override
        public List<Subscription> findByUserEmail(String email) {
            return List.of(subscription);
        }

        @Override
        public void deleteById(Long id) {
            // no-op for this test
        }
    }

    private static class FakePaymentGateway implements IPaymentGateway {
        private final boolean shouldSucceed;

        private String lastEmail;
        private double lastAmount;
        private String lastDescription;

        private FakePaymentGateway(boolean shouldSucceed) {
            this.shouldSucceed = shouldSucceed;
        }

        @Override
        public boolean charge(String email, double amount, String description) {
            this.lastEmail = email;
            this.lastAmount = amount;
            this.lastDescription = description;
            return shouldSucceed;
        }
    }

    @Test
    void shouldProcessPaymentSuccessfully() {
        // Arrange
        Subscription subscription = new Subscription(
                "Spotify",
                119.00,
                Cycle.MONTHLY,
                LocalDate.now()
        );

        FakeSubscriptionRepository subscriptionRepository = new FakeSubscriptionRepository(subscription);
        FakePaymentGateway paymentGateway = new FakePaymentGateway(true);

        ProcessPaymentUseCase useCase = new ProcessPaymentUseCase(paymentGateway, subscriptionRepository);

        // Act
        boolean result = useCase.execute("mariano@correo.com", "Spotify");

        // Assert
        assertTrue(result, "The payment should be processed successfully.");
        assertEquals("mariano@correo.com", paymentGateway.lastEmail);
        assertEquals(119.00, paymentGateway.lastAmount);
        assertNotNull(paymentGateway.lastDescription);
    }

    @Test
    void shouldFailWhenPaymentIsRejected() {
        // Arrange
        Subscription subscription = new Subscription(
                "Spotify",
                119.00,
                Cycle.MONTHLY,
                LocalDate.now()
        );

        FakeSubscriptionRepository subscriptionRepository = new FakeSubscriptionRepository(subscription);
        FakePaymentGateway paymentGateway = new FakePaymentGateway(false);

        ProcessPaymentUseCase useCase = new ProcessPaymentUseCase(paymentGateway, subscriptionRepository);

        // Act
        boolean result = useCase.execute("mariano@correo.com", "Spotify");

        // Assert
        assertFalse(result, "The payment should be rejected.");
    }
}