package com.subscrybe.application.usecases;

import com.subscrybe.application.ports.out.IPasswordHasher;
import com.subscrybe.application.ports.out.ITokenGenerator;
import com.subscrybe.application.ports.out.IUserRepository;
import com.subscrybe.domain.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginUserUseCaseTest {

    private LoginUserUseCase loginUserUseCase;
    private IUserRepository fakeUserRepository;
    private IPasswordHasher fakePasswordHasher;
    private ITokenGenerator fakeTokenGenerator;

    @BeforeEach
    void setUp() {
        // 1. Configuramos nuestros "Fakes" (Simuladores de infraestructura)
        fakeUserRepository = new IUserRepository() {
            @Override
            public void save(User user) {}

            @Override
            public User findByEmail(String email) {
                if ("mariano@correo.com".equals(email)) {
                    // Simulamos que la base de datos devuelve este usuario
                    return new User("Mariano", "mariano@correo.com", "hashed_password");
                }
                return null;
            }

            @Override
            public boolean existsByEmail(String email) {
                return "mariano@correo.com".equals(email);
            }
        };

        fakePasswordHasher = new IPasswordHasher() {
            @Override
            public String hash(String rawPassword) {
                return "hashed_" + rawPassword;
            }

            @Override
            public boolean matches(String rawPassword, String hashedPassword) {
                // Simulamos la verificación: la contraseña cruda debe coincidir con la encriptada
                return hashedPassword.equals("hashed_" + rawPassword);
            }
        };

        fakeTokenGenerator = user -> "fake-jwt-token-for-" + user.getName();

        // 2. Inyectamos los fakes en nuestro Caso de Uso real
        loginUserUseCase = new LoginUserUseCase(fakeUserRepository, fakePasswordHasher, fakeTokenGenerator);
    }

    @Test
    void execute_WithValidCredentials_ReturnsToken() {
        // Arrange (Preparar)
        String email = "mariano@correo.com";
        String password = "password";

        // Act (Actuar)
        String token = loginUserUseCase.execute(email, password);

        // Assert (Afirmar)
        assertNotNull(token);
        assertEquals("fake-jwt-token-for-Mariano", token);
    }

    @Test
    void execute_WithInvalidEmail_ThrowsException() {
        // Arrange
        String unregisteredEmail = "fantasma@correo.com";
        String password = "password";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            loginUserUseCase.execute(unregisteredEmail, password);
        });

        assertEquals("Credenciales inválidas.", exception.getMessage());
    }

    @Test
    void execute_WithInvalidPassword_ThrowsException() {
        // Arrange
        String email = "mariano@correo.com";
        String wrongPassword = "wrongpassword";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            loginUserUseCase.execute(email, wrongPassword);
        });

        assertEquals("Credenciales inválidas.", exception.getMessage());
    }
}