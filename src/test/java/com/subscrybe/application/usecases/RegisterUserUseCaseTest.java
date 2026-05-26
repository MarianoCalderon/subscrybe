package com.subscrybe.application.usecases;

import com.subscrybe.application.ports.out.IUserRepository;
import com.subscrybe.domain.entities.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RegisterUserUseCaseTest {

    // 1. Creamos nuestro Fake Repository para simular la base de datos
    class FakeUserRepository implements IUserRepository {
        private boolean userSaved = false;
        private boolean simulateEmailExists = false;

        @Override
        public void save(User user) {
            this.userSaved = true;
        }

        @Override
        public boolean existsByEmail(String email) {
            return simulateEmailExists;
        }

        @Override
        public User findByEmail(String email) {
            return null; // En los tests, no necesitamos implementar lógica aquí
        }


        // Métodos de ayuda solo para el test
        public void setSimulateEmailExists(boolean exists) {
            this.simulateEmailExists = exists;
        }

        public boolean wasUserSaved() {
            return userSaved;
        }
    }

    @Test
    void shouldRegisterNewUserSuccessfully() {
        // Arrange
        FakeUserRepository fakeRepo = new FakeUserRepository();
        RegisterUserUseCase useCase = new RegisterUserUseCase(fakeRepo);

        // Act
        User result = useCase.execute("Mariano", "mariano@correo.com");

        // Assert: Verificamos que devuelva el usuario y que el repositorio haya sido llamado
        assertNotNull(result, "El usuario devuelto no debería ser nulo");
        assertEquals("Mariano", result.getName());
        assertTrue(fakeRepo.wasUserSaved(), "El usuario debió haberse guardado en el repositorio");
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Arrange
        FakeUserRepository fakeRepo = new FakeUserRepository();
        fakeRepo.setSimulateEmailExists(true); // Simulamos que el correo ya está en la BD
        RegisterUserUseCase useCase = new RegisterUserUseCase(fakeRepo);

        // Act & Assert: Verificamos que se lance la excepción correcta
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            useCase.execute("Mariano", "mariano@correo.com");
        });

        // Verificamos que el mensaje de error sea exacto y que NO se haya intentado guardar
        assertEquals("El correo ya está registrado.", exception.getMessage());
        assertFalse(fakeRepo.wasUserSaved(), "El repositorio NO debió guardar nada");
    }
}