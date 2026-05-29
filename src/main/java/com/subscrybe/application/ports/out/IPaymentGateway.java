package com.subscrybe.application.ports.out;

public interface IPaymentGateway {
    boolean charge(String userEmail, double amount, String description);
}