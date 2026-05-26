package com.subscrybe.domain.entities;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class SubscriptionTest {

    @Test
    void shouldCalculateNextPaymentDateForMonthlySubscription() {
        // Arrange: Preparamos los datos iniciales
        LocalDate startDate = LocalDate.of(2026, 5, 10);
        Subscription netflix = new Subscription("Netflix", 250.0, Cycle.MONTHLY, startDate);

        // Act: Ejecutamos el método que queremos probar simulando que hoy es 25 de mayo
        LocalDate nextPayment = netflix.calculateNextPaymentDate(LocalDate.of(2026, 5, 25));

        // Assert: Comprobamos que el resultado sea el esperado (10 de junio)
        assertEquals(LocalDate.of(2026, 6, 10), nextPayment);
    }
}