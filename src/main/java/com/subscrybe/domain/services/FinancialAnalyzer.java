package com.subscrybe.domain.services;

import com.subscrybe.domain.entities.Subscription;

public class FinancialAnalyzer {

    // Esta es una Regla de Negocio Pura (Enterprise Domain)
    public String evaluateSubscription(Subscription subscription, int daysUsedPerWeek) {

        // Si el usuario usa el servicio menos de 2 días a la semana, recomendamos cancelar
        if (daysUsedPerWeek < 2) {
            return "CANCEL";
        }

        // De lo contrario, el uso justifica el gasto y sugerimos mantenerla
        return "KEEP";
    }
}