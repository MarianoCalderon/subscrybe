package com.subscrybe.infrastructure.config;

import com.subscrybe.application.ports.out.IUserRepository;
import com.subscrybe.application.ports.out.ISubscriptionRepository;
import com.subscrybe.application.usecases.RegisterUserUseCase;
import com.subscrybe.application.usecases.AddSubscriptionUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    // Bean del Usuario (El que ya tenías)
    @Bean
    public RegisterUserUseCase registerUserUseCase(IUserRepository userRepository) {
        return new RegisterUserUseCase(userRepository);
    }

    // ¡Nuevo Bean de Suscripciones!
    @Bean
    public AddSubscriptionUseCase addSubscriptionUseCase(ISubscriptionRepository subscriptionRepository) {
        return new AddSubscriptionUseCase(subscriptionRepository);
    }
}