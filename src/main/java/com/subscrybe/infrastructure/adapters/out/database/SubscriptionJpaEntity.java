package com.subscrybe.infrastructure.adapters.out.database;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "subscriptions")
public class SubscriptionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double cost;

    // Guardamos el Enum como un texto (MONTHLY, ANNUAL, etc.) en la base de datos
    @Column(nullable = false)
    private String billingCycle;

    @Column(nullable = false)
    private LocalDate startDate;

    // Constructor vacío para JPA
    public SubscriptionJpaEntity() {}

    public SubscriptionJpaEntity(String name, double cost, String billingCycle, LocalDate startDate) {
        this.name = name;
        this.cost = cost;
        this.billingCycle = billingCycle;
        this.startDate = startDate;
    }

    // Getters para el Adaptador
    public String getName() { return name; }
    public double getCost() { return cost; }
    public String getBillingCycle() { return billingCycle; }
    public LocalDate getStartDate() { return startDate; }
}