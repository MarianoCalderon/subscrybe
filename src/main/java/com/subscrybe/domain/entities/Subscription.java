package com.subscrybe.domain.entities;

import java.time.LocalDate;

public class Subscription {
    private String name;
    private double cost;
    private Cycle billingCycle;
    private LocalDate startDate;

    public Subscription(String name, double cost, Cycle billingCycle, LocalDate startDate) {
        this.name = name;
        this.cost = cost;
        this.billingCycle = billingCycle;
        this.startDate = startDate;
    }

    public LocalDate calculateNextPaymentDate(LocalDate currentDate) {
        if (billingCycle == Cycle.MONTHLY) {
            LocalDate nextDate = startDate;
            while (!nextDate.isAfter(currentDate)) {
                nextDate = nextDate.plusMonths(1);
            }
            return nextDate;
        }
        return startDate;
    }

    // Getters originales
    public double getCost() {
        return cost;
    }

    public String getName() {
        return name;
    }

    // ¡Nuevos Getters agregados para que el Adaptador pueda leer la info!
    public Cycle getBillingCycle() {
        return billingCycle;
    }

    public LocalDate getStartDate() {
        return startDate;
    }
}