package com.subscrybe.domain.services;

import com.subscrybe.domain.entities.AnalysisResult;
import com.subscrybe.domain.entities.Cycle;
import com.subscrybe.domain.entities.Subscription;

public class FinancialAnalyzer {

    private static final double WEEKS_PER_MONTH = 4.33;
    private static final double HIGH_COST_PER_SESSION = 50.0;
    private static final double MEDIUM_COST_PER_SESSION = 25.0;

    public AnalysisResult evaluateSubscription(Subscription subscription, int daysUsedPerWeek) {
        double monthlyCost = normalizeToCostMonthly(subscription);

        if (daysUsedPerWeek == 0) {
            return new AnalysisResult("CANCEL",
                    "Estás pagando por un servicio que no usas. Cancélalo de inmediato.",
                    monthlyCost, 0.0, daysUsedPerWeek);
        }

        double sessionsPerMonth = daysUsedPerWeek * WEEKS_PER_MONTH;
        double costPerSession = monthlyCost / sessionsPerMonth;

        if (daysUsedPerWeek < 2) {
            return new AnalysisResult("CANCEL",
                    String.format("Uso muy bajo (%d día/s por semana). No justifica el gasto de $%.2f/mes.",
                            daysUsedPerWeek, monthlyCost),
                    monthlyCost, costPerSession, daysUsedPerWeek);
        }

        if (costPerSession > HIGH_COST_PER_SESSION) {
            return new AnalysisResult("CANCEL",
                    String.format("El costo por sesión ($%.2f) es demasiado alto para el uso que le das.",
                            costPerSession),
                    monthlyCost, costPerSession, daysUsedPerWeek);
        }

        if (costPerSession > MEDIUM_COST_PER_SESSION) {
            return new AnalysisResult("REDUCE",
                    String.format("Costo por sesión moderado ($%.2f). Considera un plan más económico o compartir la suscripción.",
                            costPerSession),
                    monthlyCost, costPerSession, daysUsedPerWeek);
        }

        return new AnalysisResult("KEEP",
                String.format("Excelente relación costo-uso. Te cuesta $%.2f por sesión. Vale la pena.",
                        costPerSession),
                monthlyCost, costPerSession, daysUsedPerWeek);
    }

    // Normaliza el costo anual a mensual para comparar en igualdad de condiciones
    private double normalizeToCostMonthly(Subscription subscription) {
        if (subscription.getBillingCycle() == Cycle.ANNUAL) {
            return subscription.getCost() / 12.0;
        }
        return subscription.getCost();
    }
}