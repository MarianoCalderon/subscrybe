package com.subscrybe.infrastructure.config;

import com.subscrybe.application.ports.out.IPaymentGateway;
import com.subscrybe.application.ports.out.IUserRepository;
import com.subscrybe.application.ports.out.ISubscriptionRepository;
import com.subscrybe.application.usecases.ProcessPaymentUseCase;
import com.subscrybe.application.usecases.RegisterUserUseCase;
import com.subscrybe.application.usecases.AddSubscriptionUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {
    @Bean
    public RegisterUserUseCase registerUserUseCase(IUserRepository userRepository) {
        return new RegisterUserUseCase(userRepository);
    }

    @Bean
    public AddSubscriptionUseCase addSubscriptionUseCase(ISubscriptionRepository subscriptionRepository) {
        return new AddSubscriptionUseCase(subscriptionRepository);
    }

    @Bean
    public ProcessPaymentUseCase processPaymentUseCase(IPaymentGateway paymentGateway, ISubscriptionRepository subscriptionRepository) {
        // Retornamos el caso de uso puro de dominio, inyectándole las dependencias técnicas
        return new ProcessPaymentUseCase(paymentGateway, subscriptionRepository);
    }
}