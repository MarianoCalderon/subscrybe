package com.subscrybe.application.usecases;

import com.subscrybe.application.ports.out.ISubscriptionRepository;
import com.subscrybe.domain.entities.AnalysisResult;
import com.subscrybe.domain.entities.Subscription;
import com.subscrybe.domain.services.FinancialAnalyzer;

public class AnalyzeSubscriptionUseCase {

    private final ISubscriptionRepository subscriptionRepository;
    private final FinancialAnalyzer financialAnalyzer;

    public AnalyzeSubscriptionUseCase(ISubscriptionRepository subscriptionRepository,
                                      FinancialAnalyzer financialAnalyzer) {
        this.subscriptionRepository = subscriptionRepository;
        this.financialAnalyzer = financialAnalyzer;
    }

    public AnalysisResult execute(String subscriptionName, int daysUsedPerWeek) {
        if (daysUsedPerWeek < 0 || daysUsedPerWeek > 7) {
            throw new IllegalArgumentException("Los días de uso por semana deben estar entre 0 y 7.");
        }

        Subscription subscription = subscriptionRepository.findByName(subscriptionName);
        if (subscription == null) {
            throw new IllegalArgumentException("No se encontró la suscripción: " + subscriptionName);
        }

        return financialAnalyzer.evaluateSubscription(subscription, daysUsedPerWeek);
    }
}
