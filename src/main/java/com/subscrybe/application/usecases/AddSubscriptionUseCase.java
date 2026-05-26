package com.subscrybe.application.usecases;
import com.subscrybe.application.ports.out.ISubscriptionRepository;
import com.subscrybe.domain.entities.Cycle;
import com.subscrybe.domain.entities.Subscription;

import java.time.LocalDate;

public class AddSubscriptionUseCase {

    // Dependemos de la abstracción (Interfaz), no de la implementación concreta
    private final ISubscriptionRepository repository;

    // Inyección de dependencias a través del constructor
    public AddSubscriptionUseCase(ISubscriptionRepository repository) {
        this.repository = repository;
    }

    // El método que orquesta la creación
    public void execute(String name, double cost, String cycleStr, String startDateStr) {
        // 1. Convertimos los datos de entrada a los tipos que usa el Dominio
        Cycle cycle = Cycle.valueOf(cycleStr);
        LocalDate startDate = LocalDate.parse(startDateStr);

        // 2. Aplicamos las reglas de negocio (Crear la entidad)
        Subscription newSubscription = new Subscription(name, cost, cycle, startDate);

        // 3. Guardamos a través del puerto de salida
        repository.save(newSubscription);
    }
}