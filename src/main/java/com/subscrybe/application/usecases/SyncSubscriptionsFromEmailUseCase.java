package com.subscrybe.application.usecases;

import com.subscrybe.application.ports.out.IEmailScanner;
import com.subscrybe.application.ports.out.ISubscriptionRepository;
import com.subscrybe.domain.entities.Subscription;
import java.util.List;

public class SyncSubscriptionsFromEmailUseCase {

    private final IEmailScanner emailScanner;
    private final ISubscriptionRepository subscriptionRepository;

    public SyncSubscriptionsFromEmailUseCase(IEmailScanner emailScanner, ISubscriptionRepository subscriptionRepository) {
        this.emailScanner = emailScanner;
        this.subscriptionRepository = subscriptionRepository;
    }

    public void execute(String emailAccount) {
        // 1. Extraemos todas las suscripciones encontradas en el correo
        List<Subscription> foundSubscriptions = emailScanner.scanInbox(emailAccount);

        // 2. Las guardamos en la base de datos
        for (Subscription sub : foundSubscriptions) {
            subscriptionRepository.save(sub);
        }
    }
}