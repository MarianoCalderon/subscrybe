package com.subscrybe.application.usecases;

import com.subscrybe.application.ports.out.IPaymentGateway;
import com.subscrybe.application.ports.out.ISubscriptionRepository;
import com.subscrybe.domain.entities.Cycle;
import com.subscrybe.domain.entities.Subscription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class ProcessPaymentUseCaseTest {

    @Mock
    private IPaymentGateway paymentGateway;

    @Mock
    private ISubscriptionRepository subscriptionRepository;

    @InjectMocks
    private ProcessPaymentUseCase processPaymentUseCase;

    @BeforeEach
    void setUp() {
        // Initializes the mocked interfaces
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldProcessPaymentSuccessfully() {
        // Arrange
        Subscription mockSub = new Subscription("Spotify", 119.00, Cycle.MONTHLY, LocalDate.now());
        when(subscriptionRepository.findByName("Spotify")).thenReturn(mockSub);
        when(paymentGateway.charge(eq("mariano@correo.com"), eq(119.00), anyString())).thenReturn(true);

        // Act
        boolean result = processPaymentUseCase.execute("mariano@correo.com", "Spotify");

        // Assert
        assertTrue(result, "The payment should be processed successfully.");
    }

    @Test
    void shouldFailWhenPaymentIsRejected() {
        // Arrange
        Subscription mockSub = new Subscription("Spotify", 119.00, Cycle.MONTHLY, LocalDate.now());
        when(subscriptionRepository.findByName("Spotify")).thenReturn(mockSub);
        // Simulate a declined card
        when(paymentGateway.charge(anyString(), anyDouble(), anyString())).thenReturn(false);

        // Act
        boolean result = processPaymentUseCase.execute("mariano@correo.com", "Spotify");

        // Assert
        assertFalse(result, "The payment should be rejected.");
    }
}