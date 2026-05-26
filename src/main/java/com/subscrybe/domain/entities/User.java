package com.subscrybe.domain.entities;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String name;
    private String email;
    // Aquí guardamos la relación entre el usuario y sus suscripciones
    private List<Subscription> subscriptions;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
        this.subscriptions = new ArrayList<>(); // Inicializamos la lista vacía
    }

    // Método para vincular una nueva suscripción al perfil
    public void addSubscription(Subscription subscription) {
        this.subscriptions.add(subscription);
    }

    // Regla de negocio: Calcular el gasto total
    public double getTotalMonthlyCost() {
        double total = 0.0;
        for (Subscription sub : subscriptions) {
            total += sub.getCost(); // Sumamos el costo de cada suscripción en la lista
        }
        return total;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}