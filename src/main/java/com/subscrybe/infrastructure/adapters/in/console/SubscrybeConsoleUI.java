package com.subscrybe.infrastructure.adapters.in.console;

import com.subscrybe.application.usecases.DeleteSubscriptionUseCase;
import com.subscrybe.application.usecases.GetSubscriptionsUseCase;
import com.subscrybe.application.usecases.SyncSubscriptionsFromEmailUseCase;
import com.subscrybe.domain.entities.Subscription;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
public class SubscrybeConsoleUI implements CommandLineRunner {

    // Inyectamos nuestros Casos de Uso (Nuestra lógica de negocio pura)
    private final SyncSubscriptionsFromEmailUseCase syncUseCase;
    private final GetSubscriptionsUseCase getSubscriptionsUseCase;
    private final DeleteSubscriptionUseCase deleteUseCase;

    public SubscrybeConsoleUI(SyncSubscriptionsFromEmailUseCase syncUseCase,
                              GetSubscriptionsUseCase getSubscriptionsUseCase,
                              DeleteSubscriptionUseCase deleteUseCase) {
        this.syncUseCase = syncUseCase;
        this.getSubscriptionsUseCase = getSubscriptionsUseCase;
        this.deleteUseCase = deleteUseCase;
    }

    @Override
    public void run(String... args) {
        // Lanzamos el menú en un hilo separado para no bloquear el servidor web (Tomcat)
        new Thread(() -> {
            try {
                Thread.sleep(2000); // Esperamos 2 segundos a que Spring termine de imprimir sus logs
                mostrarMenuInteractivo();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void mostrarMenuInteractivo() {
        Scanner scanner = new Scanner(System.in);
        String currentUserEmail = null;

        while (true) {
            System.out.println("\n========================================");
            System.out.println("    🚀 B I E N V E N I D O   A   S U B S C R Y B E");
            System.out.println("========================================");

            if (currentUserEmail == null) {
                System.out.println("Usuario actual: [No autenticado]");
            } else {
                System.out.println("Usuario actual: " + currentUserEmail);
            }

            System.out.println("\nSelecciona una opción:");
            System.out.println("1. 👤 Registrarse / Iniciar Sesión");
            System.out.println("2. 📧 Sincronizar Bandeja de Entrada (Google API)");
            System.out.println("3. 📊 Ver mi Tablero de Suscripciones");
            System.out.println("4. 📉 Análisis Financiero (En desarrollo por tu equipo)");
            System.out.println("5. 💳 Pagar una Suscripción (En desarrollo por tu equipo)");
            System.out.println("6. 🗑️  Eliminar una Suscripción");
            System.out.println("7. 🚪 Salir");
            System.out.print("\nTu elección: ");

            String opcion = scanner.nextLine();

            try {
                switch (opcion) {
                    case "1":
                        System.out.print("Ingresa tu correo electrónico: ");
                        currentUserEmail = scanner.nextLine();
                        System.out.println("✅ Sesión iniciada para: " + currentUserEmail);
                        break;

                    case "2":
                        if (currentUserEmail == null) {
                            System.out.println("⚠️ Primero debes iniciar sesión (Opción 1).");
                            break;
                        }
                        System.out.println("⏳ Abriendo navegador para solicitar permisos a Google...");
                        syncUseCase.execute(currentUserEmail);
                        System.out.println("✅ Escaneo completado.");
                        break;

                    case "3":
                        if (currentUserEmail == null) {
                            System.out.println("⚠️ Primero debes iniciar sesión (Opción 1).");
                            break;
                        }
                        System.out.println("\n--- 📊 TU TABLERO DE SUBSCRIPCIONES ---");
                        List<Subscription> misSuscripciones = getSubscriptionsUseCase.execute(currentUserEmail);

                        if (misSuscripciones.isEmpty()) {
                            System.out.println("No tienes suscripciones registradas. ¡Usa la opción 2 para escanear!");
                        } else {
                            double totalMensual = 0;
                            // En tu entidad Subscription debe existir un getId(), ajusta si es necesario
                            for (Subscription sub : misSuscripciones) {
                                // Nota: Si tu dominio Subscription no tiene getId(), puedes imprimir solo el nombre
                                System.out.println("- [" + sub.getName() + "] | Costo: $" + sub.getCost() + " | Fecha de corte: " + sub.getStartDate());
                                totalMensual += sub.getCost();
                            }
                            System.out.println("---------------------------------------");
                            System.out.println("💰 GASTO TOTAL MENSUAL: $" + totalMensual);
                        }
                        break;

                    case "4":
                        System.out.println("🚧 Módulo de Análisis Financiero en construcción.");
                        System.out.println("Aquí haremos las preguntas de uso (¿Cuántas veces a la semana lo usas?)");
                        break;

                    case "5":
                        System.out.println("🚧 Módulo de Pagos en construcción.");
                        System.out.println("Aquí conectaremos el IGateway de pagos (Stripe/PayPal).");
                        break;

                    case "6":
                        System.out.println("Ingresa el ID de la suscripción que deseas eliminar en la base de datos:");
                        String idStr = scanner.nextLine();
                        deleteUseCase.execute(Long.parseLong(idStr));
                        System.out.println("✅ Suscripción eliminada correctamente.");
                        break;

                    case "7":
                        System.out.println("Saliendo de Subscrybe... ¡Hasta pronto!");
                        System.exit(0);
                        break;

                    default:
                        System.out.println("⚠️ Opción no válida. Intenta de nuevo.");
                }
            } catch (Exception e) {
                System.out.println("❌ Ocurrió un error: " + e.getMessage());
            }
        }
    }
}