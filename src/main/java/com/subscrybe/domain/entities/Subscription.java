package com.subscrybe.domain.entities;

import java.time.LocalDate;

public class Subscription {
    // 👇 1. Agregamos el ID para identificar la suscripción
    private Long id;
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

    // 👇 2. Agregamos el Getter y Setter para el ID
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Cycle getBillingCycle() {
        return billingCycle;
    }

    public LocalDate getStartDate() {
        return startDate;
    }
}