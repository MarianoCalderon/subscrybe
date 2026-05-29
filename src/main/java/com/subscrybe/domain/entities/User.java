package com.subscrybe.domain.entities;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String name;
    private String email;
    private String password; // Nuevo campo para la autenticación
    private List<Subscription> subscriptions;

    // Actualizamos el constructor para requerir la contraseña
    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
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

    public String getPassword() {
        return password;
    }
}