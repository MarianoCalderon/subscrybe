package com.subscrybe.infrastructure.adapters.out.database;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class UserJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    // Nuevo campo para guardar la contraseña encriptada
    @Column(nullable = false)
    private String password;

    // Constructor vacío requerido por JPA
    public UserJpaEntity() {}

    // Constructor actualizado con el password
    public UserJpaEntity(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
}