package com.subscrybe.infrastructure.config;

import com.subscrybe.application.ports.out.IUserRepository;
import com.subscrybe.application.ports.out.ISubscriptionRepository;
import com.subscrybe.application.usecases.AddSubscriptionUseCase;
import com.subscrybe.application.usecases.AnalyzeSubscriptionUseCase;
import com.subscrybe.application.usecases.RegisterUserUseCase;
import com.subscrybe.domain.services.FinancialAnalyzer;
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
    public AnalyzeSubscriptionUseCase analyzeSubscriptionUseCase(ISubscriptionRepository subscriptionRepository) {
        return new AnalyzeSubscriptionUseCase(subscriptionRepository, new FinancialAnalyzer());
    }
}