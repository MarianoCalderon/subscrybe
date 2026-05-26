package com.subscrybe.application.ports.out;

public interface IPaymentGateway {
    // Retorna true si el cobro fue exitoso, false si fue rechazado
    boolean charge(String userEmail, double amount, String description);
}