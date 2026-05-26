package com.subscrybe.application.usecases;

import com.subscrybe.application.ports.out.IEmailReceiptExtractor;
import com.subscrybe.application.ports.out.ISubscriptionRepository;
import com.subscrybe.domain.entities.Cycle;
import com.subscrybe.domain.entities.Subscription;

import java.time.LocalDate;
import java.util.List;

public class SyncReceiptsUseCase {

    // Inyectamos nuestras dos interfaces (Puertos de salida)
    private final IEmailReceiptExtractor emailExtractor;
    private final ISubscriptionRepository repository;

    public SyncReceiptsUseCase(IEmailReceiptExtractor emailExtractor, ISubscriptionRepository repository) {
        this.emailExtractor = emailExtractor;
        this.repository = repository;
    }

    // El método que ejecuta la sincronización
    public boolean execute(String email, String subscriptionName) {
        // 1. Pedimos las fechas al extractor (la implementación real será un detalle de infraestructura)
        List<LocalDate> dates = emailExtractor.extractRenewalDates(email, subscriptionName);

        // Si no encontró recibos, terminamos el proceso
        if (dates == null || dates.isEmpty()) {
            return false;
        }

        // 2. Tomamos la fecha más reciente (asumiendo que la lista viene ordenada)
        LocalDate latestDate = dates.get(dates.size() - 1);

        // 3. Creamos la entidad con la fecha encontrada
        // (El costo lo dejamos en 0.0 por ahora, pues el recibo principal nos dio la fecha)
        Subscription updatedSubscription = new Subscription(subscriptionName, 0.0, Cycle.MONTHLY, latestDate);

        // 4. Guardamos a través del repositorio
        repository.save(updatedSubscription);

        return true;
    }
}