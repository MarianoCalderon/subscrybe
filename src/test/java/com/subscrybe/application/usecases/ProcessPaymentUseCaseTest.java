package com.subscrybe.application.usecases;

import com.subscrybe.application.ports.out.IPaymentGateway;
import com.subscrybe.application.ports.out.ISubscriptionRepository;
import com.subscrybe.domain.entities.Cycle;
import com.subscrybe.domain.entities.Subscription;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ProcessPaymentUseCaseTest {

    // Simulamos la pasarela de pagos (Stripe/PayPal)
    class FakePaymentGateway implements IPaymentGateway {
        private boolean shouldSucceed = true;

        public void setShouldSucceed(boolean shouldSucceed) {
            this.shouldSucceed = shouldSucceed;
        }

        @Override
        public boolean charge(String userEmail, double amount, String description) {
            return shouldSucceed;
        }
    }

    // Simulamos la base de datos
    class FakeSubscriptionRepository implements ISubscriptionRepository {
        @Override
        public void save(Subscription subscription) {}

        // Simulamos encontrar la suscripción en la BD
        public Subscription findByName(String name) {
            return new Subscription(name, 129.0, Cycle.MONTHLY, LocalDate.now());
        }
    }

    @Test
    void shouldProcessPaymentSuccessfully() {
        // Arrange
        FakePaymentGateway fakeGateway = new FakePaymentGateway();
        FakeSubscriptionRepository fakeRepo = new FakeSubscriptionRepository();
        ProcessPaymentUseCase useCase = new ProcessPaymentUseCase(fakeGateway, fakeRepo);

        // Act
        boolean result = useCase.execute("mariano@correo.com", "Spotify");

        // Assert
        assertTrue(result, "El pago debió procesarse exitosamente");
    }

    @Test
    void shouldFailWhenPaymentIsRejected() {
        // Arrange
        FakePaymentGateway fakeGateway = new FakePaymentGateway();
        fakeGateway.setShouldSucceed(false); // Simulamos que la tarjeta fue rechazada
        FakeSubscriptionRepository fakeRepo = new FakeSubscriptionRepository();
        ProcessPaymentUseCase useCase = new ProcessPaymentUseCase(fakeGateway, fakeRepo);

        // Act
        boolean result = useCase.execute("mariano@correo.com", "Spotify");

        // Assert
        assertFalse(result, "El pago debió ser rechazado por la pasarela");
    }
}