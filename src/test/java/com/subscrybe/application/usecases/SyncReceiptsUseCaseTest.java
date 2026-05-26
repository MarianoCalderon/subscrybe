package com.subscrybe.application.usecases;

import com.subscrybe.application.ports.out.IEmailReceiptExtractor;
import com.subscrybe.application.ports.out.ISubscriptionRepository;
import com.subscrybe.domain.entities.Subscription;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SyncReceiptsUseCaseTest {

    // 1. Simulamos el extractor de correos
    class FakeEmailExtractor implements IEmailReceiptExtractor {
        @Override
        public List<LocalDate> extractRenewalDates(String email, String service) {
            // Simulamos que encontró dos pagos recientes de Netflix en el correo
            return Arrays.asList(
                    LocalDate.of(2026, 4, 15),
                    LocalDate.of(2026, 5, 15)
            );
        }
    }

    // 2. Simulamos la base de datos
    class FakeRepository implements ISubscriptionRepository {
        boolean wasUpdated = false;
        @Override
        public void save(Subscription subscription) {
            this.wasUpdated = true;
        }
        @Override
        public Subscription findByName(String name) {
            return null; // Solo para que compile, ya que estos tests no usan este método
        }
    }

    @Test
    void shouldSyncAndSaveLatestPaymentDateFromEmails() {
        // Arrange
        FakeEmailExtractor fakeExtractor = new FakeEmailExtractor();
        FakeRepository fakeRepo = new FakeRepository();
        SyncReceiptsUseCase useCase = new SyncReceiptsUseCase(fakeExtractor, fakeRepo);

        // Act: Le pedimos que sincronice los recibos de "Netflix"
        boolean result = useCase.execute("usuario@correo.com", "Netflix");

        // Assert: Verificamos que el proceso fue exitoso y se mandó a guardar a la base de datos
        assertTrue(result, "La sincronización debió ser exitosa");
        assertTrue(fakeRepo.wasUpdated, "La nueva fecha debió guardarse en el repositorio");
    }
}