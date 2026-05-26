package com.subscrybe.infrastructure.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Collections;

@SpringBootApplication
@ComponentScan(basePackages = "com.subscrybe")
// ¡Le decimos a Spring exactamente dónde buscar nuestros archivos de BD!
@EnableJpaRepositories(basePackages = "com.subscrybe.infrastructure.adapters.out.database")
@EntityScan(basePackages = "com.subscrybe.infrastructure.adapters.out.database")
public class SubscrybeApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(SubscrybeApplication.class);

        app.setDefaultProperties(Collections.singletonMap("server.port", "8081"));
        app.run(args);

        System.out.println("¡Subscrybe está corriendo y escuchando en el puerto 8081!");
    }
}