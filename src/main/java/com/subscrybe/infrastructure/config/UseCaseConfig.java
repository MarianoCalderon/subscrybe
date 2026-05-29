package com.subscrybe.infrastructure.config;

import com.subscrybe.application.ports.out.*;
import com.subscrybe.application.usecases.AddSubscriptionUseCase;
import com.subscrybe.application.usecases.AnalyzeSubscriptionUseCase;
import com.subscrybe.application.usecases.DeleteSubscriptionUseCase;
import com.subscrybe.application.usecases.GetSubscriptionsUseCase;
import com.subscrybe.application.usecases.LoginUserUseCase;
import com.subscrybe.application.usecases.ProcessPaymentUseCase;
import com.subscrybe.application.usecases.RegisterUserUseCase;
import com.subscrybe.application.usecases.SyncSubscriptionsFromEmailUseCase;
import com.subscrybe.domain.services.FinancialAnalyzer;
import com.subscrybe.infrastructure.adapters.out.email.GmailApiScannerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    // Bean del Usuario
    @Bean
    public RegisterUserUseCase registerUserUseCase(
            IUserRepository userRepository,
            IPasswordHasher passwordHasher) {
        return new RegisterUserUseCase(userRepository, passwordHasher);
    }

    // --- BEANS DE SUSCRIPCIONES ---

    @Bean
    public AddSubscriptionUseCase addSubscriptionUseCase(ISubscriptionRepository subscriptionRepository) {
        return new AddSubscriptionUseCase(subscriptionRepository);
    }

    @Bean
    public ProcessPaymentUseCase processPaymentUseCase(IPaymentGateway paymentGateway, ISubscriptionRepository subscriptionRepository) {
        // Retornamos el caso de uso puro de dominio, inyectándole las dependencias técnicas
        return new ProcessPaymentUseCase(paymentGateway, subscriptionRepository);
    }

    // 👇 NUEVO BEAN: Para listar las suscripciones en tu tablero
    @Bean
    public GetSubscriptionsUseCase getSubscriptionsUseCase(ISubscriptionRepository subscriptionRepository) {
        return new GetSubscriptionsUseCase(subscriptionRepository);
    }

    // 👇 NUEVO BEAN: Para eliminar suscripciones
    @Bean
    public DeleteSubscriptionUseCase deleteSubscriptionUseCase(ISubscriptionRepository subscriptionRepository) {
        return new DeleteSubscriptionUseCase(subscriptionRepository);
    }

    // --- EL NUEVO AJUSTE: El Bean para el Login ---
    @Bean
    public LoginUserUseCase loginUserUseCase(IUserRepository userRepository, IPasswordHasher passwordHasher, ITokenGenerator tokenGenerator) {
        return new LoginUserUseCase(userRepository, passwordHasher, tokenGenerator);
    }

    @Bean
    public SyncSubscriptionsFromEmailUseCase syncSubscriptionsFromEmailUseCase(
            GmailApiScannerAdapter gmailScannerAdapter,
            ISubscriptionRepository repo) {
        return new SyncSubscriptionsFromEmailUseCase(gmailScannerAdapter, repo);
    }

    @Bean
    public AnalyzeSubscriptionUseCase analyzeSubscriptionUseCase(ISubscriptionRepository subscriptionRepository) {
        return new AnalyzeSubscriptionUseCase(subscriptionRepository, new FinancialAnalyzer());
    }
}