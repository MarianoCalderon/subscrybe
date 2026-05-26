# Subscrybe - Subscription Tracker Backend

¡Bienvenido al repositorio central del backend de **Subscrybe**! Este proyecto está diseñado bajo los principios de **Arquitectura Limpia (Clean Architecture)** y **Diseño Guiado por el Dominio (DDD)**, asegurando que las reglas de negocio permanezcan completamente aisladas, testables e independientes de frameworks o bases de datos externas.

---

# Arquitectura del Proyecto

El código está estructurado en capas concéntricas para respetar la inversión de dependencias y el Principio de Responsabilidad Única (SRP):

```text
src/main/java/com/subscrybe/
│
├── domain/                      # Capa del Dominio (0 dependencias de frameworks)
│   ├── entities/                # Entidades ricas (User, Subscription, Cycle)
│   └── exceptions/              # Excepciones de negocio unificadas
│
├── application/                 # Capa de Aplicación (Casos de Uso)
│   ├── usecases/                # Orquestadores (RegisterUserUseCase, AddSubscriptionUseCase)
│   └── ports/                   # Contratos de interfaz (Puertos de salida)
│       └── out/                 # IUserRepository, ISubscriptionRepository
│
└── infrastructure/              # Capa de Infraestructura (Detalles técnicos)
    ├── adapters/                # Adaptadores concretos
    │   ├── controllers/         # Controladores Web (REST API con Spring)
    │   └── database/            # Persistencia (JPA Entities, Spring Data, Adapters)
    └── config/                  # Inyección de dependencias pura (UseCaseConfig)
```

---

# Requisitos Previos

Antes de levantar el proyecto, asegúrate de tener instalado en tu máquina local:

- Java Development Kit (JDK) 17 o superior.
- Docker Desktop (para la gestión simplificada de la base de datos).
- Un IDE profesional (se recomienda IntelliJ IDEA).

---

# Configuración del Entorno de Desarrollo

Sigue estos pasos para clonar, configurar y correr el proyecto localmente:

## 1. Levantar la Base de Datos Blindada (Docker)

El proyecto incluye un entorno aislado de PostgreSQL en Docker para evitar conflictos de versiones locales.

Levanta el contenedor ejecutando el siguiente comando en tu terminal (en la raíz del proyecto):

```bash
docker-compose up -d
```

> **Nota:** Esto expondrá la base de datos en el puerto `5434` de tu localhost para evitar colisiones con otras instancias locales de PostgreSQL (puerto por defecto `5432`).

### Credenciales de Conexión

| Parámetro | Valor |
|---|---|
| Host | localhost |
| Port | 5434 |
| Database Name | subscrybe_pro |
| User | admin_pro |
| Password | root123 |

---

## 2. Ejecutar la Aplicación Spring Boot

Una vez que el contenedor de Docker esté activo (`Running`), puedes arrancar el backend desde tu IDE ejecutando la clase principal `SubscrybeApplication` o directamente desde la terminal con Maven Wrapper:

```bash
./mvnw spring-boot:run
```

El servidor web iniciará en:

```text
http://localhost:8081
```

---

# Pruebas de Endpoints (API)

Puedes interactuar y probar el flujo completo de los endpoints utilizando herramientas como Postman o directamente desde PowerShell mediante los siguientes comandos de ejemplo:

## A. Registrar un Nuevo Usuario

```powershell
Invoke-RestMethod `
  -Uri "http://localhost:8081/api/users/register?name=Mariano&email=mariano@correo.com" `
  -Method POST
```

---

## B. Agregar una Suscripción (Relación Uno a Muchos)

```powershell
Invoke-RestMethod `
  -Uri "http://localhost:8081/api/subscriptions/add?name=Spotify&cost=119.00&cycle=MONTHLY&startDate=2026-05-26" `
  -Method POST
```

---

# Pruebas Unitarias e Inversión de Dependencias (TDD)

Este proyecto promueve un desarrollo seguro mediante pruebas unitarias rigurosas y la metodología **TDD**.

Dado que el dominio y la aplicación no dependen de Spring, las pruebas de los Casos de Uso se ejecutan en milisegundos utilizando **Fake Repositories** en lugar de mocks pesados o conexiones reales a bases de datos.

Para ejecutar la suite completa de pruebas desde la terminal:

```bash
./mvnw test
```

---

#  Reglas de Colaboración para el Equipo

Para mantener el código limpio, escalable y evitar conflictos en los merges de Git, todos los miembros del equipo deben seguir estas directrices:

## El Dominio es Sagrado

No agregues anotaciones de Spring (`@Component`, `@Autowired`, `@Entity`, etc.) dentro de los paquetes `domain` o `application`.

Toda inyección de dependencias se realiza de forma desacoplada en la capa de `infrastructure/config`.

---

## Flujo de Trabajo: Cómo agregar nuevas funcionalidades (Inside-Out)

Para mantener la Arquitectura Limpia intacta, cualquier nueva funcionalidad (ej. Módulo de Pagos, Módulo de Notificaciones) debe desarrollarse estritamente desde el núcleo hacia los detalles externos. Sigue este orden de 6 pasos:

### Paso 1: El Dominio (`domain/entities`)
Crea tu entidad pura de negocio. 
* **Regla:** Cero dependencias externas. Usa tipos nativos de Java y clases propias. Añade el comportamiento y validaciones propias de la entidad aquí.

### Paso 2: El Contrato / Puerto (`application/ports/out`)
Crea la interfaz (el contrato) que dictará qué necesita el dominio de la infraestructura (por ejemplo, buscar, guardar, borrar).
* **Regla:** La interfaz solo debe recibir y devolver objetos del Dominio, nunca objetos de base de datos o DTOs.

### Paso 3: El Caso de Uso (`application/usecases`)
Crea la clase que orquesta la regla de negocio.
* **Regla:** Inyecta la interfaz creada en el Paso 2 por constructor. Valida las reglas de negocio y delega el guardado a la interfaz. Crea aquí mismo tu prueba unitaria.

### Paso 4: La Persistencia (`infrastructure/adapters/out/database`)
Aquí creas cómo se guardará físicamente.
1. Crea la `@Entity` de JPA (`NombreJpaEntity`).
2. Crea el repositorio de `SpringData` (`JpaRepository`).
3. Crea el **Adaptador** (`NombreRepositoryAdapter`) que implemente la interfaz del Paso 2. Traduce de Entidad de Dominio a Entidad JPA y viceversa.

### Paso 5: El Controlador REST (`infrastructure/adapters/controllers`)
Crea el `@RestController` para exponer la API.
* **Regla:** El controlador solo debe recibir la petición HTTP, mapear los parámetros y llamar al método `execute()` del Caso de Uso. El controlador *nunca* procesa reglas de negocio.

### Paso 6: La Configuración (`infrastructure/config/UseCaseConfig`)
Avisa a Spring cómo armar el rompecabezas. Crea un `@Bean` que instancie tu Caso de Uso (Paso 3) inyectándole el Adaptador (Paso 4).
