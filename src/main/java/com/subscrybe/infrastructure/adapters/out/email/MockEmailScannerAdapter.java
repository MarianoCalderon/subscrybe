package com.subscrybe.infrastructure.adapters.out.email;

import com.subscrybe.application.ports.out.IEmailScanner;
import com.subscrybe.domain.entities.Cycle;
import com.subscrybe.domain.entities.Subscription;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Component
public class MockEmailScannerAdapter implements IEmailScanner {

    @Override
    public List<Subscription> scanInbox(String emailAccount) {
        System.out.println("Hackeando... digo, escaneando el correo de: " + emailAccount);

        // Fingimos que encontramos Netflix y Spotify en sus recibos
        return Arrays.asList(
                new Subscription("Netflix", 250.0, Cycle.MONTHLY, LocalDate.now().plusDays(12)),
                new Subscription("Spotify", 129.0, Cycle.MONTHLY, LocalDate.now().plusDays(4))
        );
    }
}