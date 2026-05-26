package com.subscrybe.domain.entities;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void shouldAddSubscriptionAndCalculateTotalMonthlyCost() {
        // Arrange: Creamos un usuario y un par de suscripciones
        User user = new User("Mariano", "mariano@correo.com");

        Subscription netflix = new Subscription("Netflix", 250.0, Cycle.MONTHLY, LocalDate.now());
        Subscription spotify = new Subscription("Spotify", 129.0, Cycle.MONTHLY, LocalDate.now());

        // Act: Vinculamos las suscripciones al perfil del usuario
        user.addSubscription(netflix);
        user.addSubscription(spotify);

        // Assert: Verificamos que el cálculo del gasto mensual sea exacto (250 + 129 = 379)
        assertEquals(379.0, user.getTotalMonthlyCost(), "El costo total debe ser la suma de las suscripciones mensuales");
    }
}