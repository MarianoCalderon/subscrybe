package com.subscrybe.infrastructure.adapters.out.payment;

import com.subscrybe.application.ports.out.IPaymentGateway;
import org.springframework.stereotype.Component;

@Component
public class StripePaymentAdapter implements IPaymentGateway {
    @Override
    public boolean charge(String userEmail, double amount, String description) {
        // Simulación de llamadas a API de Stripe
        System.out.println("=== INICIANDO TRANSACCIÓN CON STRIPE ===");
        System.out.println("Cobrando a: " + userEmail);
        System.out.println("Monto: $" + amount);
        System.out.println("Concepto: " + description);

        try {
            Thread.sleep(1000);
            System.out.println("=== PAGO APROBADO ===");
            return true;
        } catch (InterruptedException e) {
            System.err.println("=== ERROR EN LA PASARELA DE PAGO ===");
            return false;
        }
    }
}