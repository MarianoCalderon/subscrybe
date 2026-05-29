package com.subscrybe.application.usecases;

import com.subscrybe.application.ports.out.IPasswordHasher;
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
            // Para que RegisterUserUseCase lance la excepción, necesita devolver un usuario si ya existe
            if (simulateEmailExists) {
                return new User("Dummy", email, "hashed_password");
            }
            return null;
        }

        // Métodos de ayuda solo para el test
        public void setSimulateEmailExists(boolean exists) {
            this.simulateEmailExists = exists;
        }

        public boolean wasUserSaved() {
            return userSaved;
        }
    }

    // 2. Creamos nuestro Fake Hasher para simular la encriptación (NUEVO)
    class FakePasswordHasher implements IPasswordHasher {
        @Override
        public String hash(String rawPassword) {
            return "hashed_" + rawPassword; // Simulamos que encripta
        }

        @Override
        public boolean matches(String rawPassword, String hashedPassword) {
            return true; // No lo usamos en el registro
        }
    }

    @Test
    void shouldRegisterNewUserSuccessfully() {
        // Arrange
        FakeUserRepository fakeRepo = new FakeUserRepository();
        FakePasswordHasher fakeHasher = new FakePasswordHasher(); // Instanciamos el hasher

        // Pasamos AMBAS dependencias al Caso de Uso
        RegisterUserUseCase useCase = new RegisterUserUseCase(fakeRepo, fakeHasher);

        // Act: Ahora pasamos la contraseña como tercer parámetro
        useCase.execute("Mariano", "mariano@correo.com", "password123");

        // Assert: Verificamos que el repositorio haya sido llamado para guardar
        assertTrue(fakeRepo.wasUserSaved(), "El usuario debió haberse guardado en el repositorio");
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Arrange
        FakeUserRepository fakeRepo = new FakeUserRepository();
        fakeRepo.setSimulateEmailExists(true); // Simulamos que el correo ya está en la BD
        FakePasswordHasher fakeHasher = new FakePasswordHasher();
        RegisterUserUseCase useCase = new RegisterUserUseCase(fakeRepo, fakeHasher);

        // Act & Assert: Verificamos que se lance la excepción correcta
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            useCase.execute("Mariano", "mariano@correo.com", "password123");
        });

        // Verificamos que el mensaje de error sea exacto y que NO se haya intentado guardar
        assertEquals("El correo ya está registrado.", exception.getMessage());
        assertFalse(fakeRepo.wasUserSaved(), "El repositorio NO debió guardar nada");
    }
}