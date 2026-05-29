package com.subscrybe.application.ports.out;

import com.subscrybe.domain.entities.Subscription;
import java.util.List;

public interface IEmailScanner {
    // Escanea todo el correo y extrae las suscripciones mágicamente
    List<Subscription> scanInbox(String emailAccount);
}