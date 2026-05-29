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

    // 👇 --- EL CAMPO NUEVO QUE SPRING DATA NECESITA --- 👇
    @Column(nullable = false)
    private String userEmail;

    // Constructor vacío para JPA
    public SubscriptionJpaEntity() {}

    // Actualizamos el constructor para incluir el correo
    public SubscriptionJpaEntity(String name, double cost, String billingCycle, LocalDate startDate, String userEmail) {
        this.name = name;
        this.cost = cost;
        this.billingCycle = billingCycle;
        this.startDate = startDate;
        this.userEmail = userEmail;
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public double getCost() { return cost; }
    public String getBillingCycle() { return billingCycle; }
    public LocalDate getStartDate() { return startDate; }

    // 👇 --- GETTER Y SETTER DEL NUEVO CAMPO --- 👇
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
}