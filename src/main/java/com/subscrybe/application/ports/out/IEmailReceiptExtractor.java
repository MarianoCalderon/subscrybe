package com.subscrybe.application.ports.out;

import java.time.LocalDate;
import java.util.List;

public interface IEmailReceiptExtractor {
    // Definimos un contrato simple: buscar recibos y devolver fechas encontradas
    List<LocalDate> extractRenewalDates(String emailAddress, String subscriptionName);
}