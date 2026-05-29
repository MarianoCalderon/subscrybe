package com.subscrybe.application.usecases;

import com.subscrybe.application.ports.out.ISubscriptionRepository;
import com.subscrybe.domain.entities.Subscription;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AddSubscriptionUseCaseTest {

    // 1. Creamos un Repositorio Falso solo para la prueba (Dependency Inversion)
    class FakeRepository implements ISubscriptionRepository {
        boolean wasSaved = false;

        @Override
        public void save(Subscription subscription) {
            this.wasSaved = true; // Simulamos que se guardó
        }
        @Override
        public Subscription findByName(String name) {
            return null; // Solo para que compile, ya que estos tests no usan este método
        }
        @Override
        public java.util.List<Subscription> findByUserEmail(String email) {
            return java.util.List.of();
        }
        @Override
        public void deleteById(Long id) {}
    }

    @Test
    void shouldSaveNewSubscriptionSuccessfully() {
        // Arrange
        FakeRepository fakeRepo = new FakeRepository();
        // Inyectamos la dependencia falsa al caso de uso (que aún no existe)
        AddSubscriptionUseCase useCase = new AddSubscriptionUseCase(fakeRepo);

        // Act
        useCase.execute("Spotify", 129.0, "MONTHLY", "2026-05-25");

        // Assert
        assertTrue(fakeRepo.wasSaved, "El repositorio debió haber sido llamado para guardar la suscripción");
    }
}