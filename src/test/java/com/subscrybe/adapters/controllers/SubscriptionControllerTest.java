package com.subscrybe.adapters.controllers;

import com.subscrybe.application.ports.out.ISubscriptionRepository;
import com.subscrybe.application.usecases.AddSubscriptionUseCase;
import com.subscrybe.application.usecases.DeleteSubscriptionUseCase;
import com.subscrybe.application.usecases.GetSubscriptionsUseCase;
import com.subscrybe.domain.entities.Subscription;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class SubscriptionControllerTest {

    // Reutilizamos el repositorio falso. ¡Excelente práctica para aislar pruebas!
    class FakeRepository implements ISubscriptionRepository {
        @Override
        public void save(Subscription subscription) {
            // Simulamos que guarda
        }
        @Override
        public Subscription findByName(String name) {
            return null;
        }
        @Override
        public java.util.List<Subscription> findByUserEmail(String email) {
            return java.util.List.of();
        }
        @Override
        public void deleteById(Long id) {}
    }

    @Test
    void shouldReturnSuccessWhenSubscriptionIsAdded() {
        // Arrange: Armamos el rompecabezas de dependencias (DI)
        FakeRepository fakeRepo = new FakeRepository();
        AddSubscriptionUseCase useCase = new AddSubscriptionUseCase(fakeRepo);
        GetSubscriptionsUseCase getUseCase = new GetSubscriptionsUseCase(fakeRepo);
        DeleteSubscriptionUseCase deleteUseCase = new DeleteSubscriptionUseCase(fakeRepo);
        SubscriptionController controller = new SubscriptionController(useCase, getUseCase, deleteUseCase);

        // Act: Simulamos la petición del usuario. ¡Ahora recibimos un ResponseEntity!
        ResponseEntity<String> response = controller.addSubscription("Netflix", 250.0, "MONTHLY", "2026-05-26");

        // Assert: Verificamos el código de estado HTTP y el contenido del mensaje
        assertEquals(200, response.getStatusCode().value(), "El código HTTP debe ser 200 OK");
        assertNotNull(response.getBody(), "El cuerpo de la respuesta no debe ser nulo");
        assertTrue(response.getBody().contains("guardada con éxito"));
    }
}