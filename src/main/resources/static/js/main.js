/**
 * COMPOSITION ROOT (Raíz de Composición)
 * --------------------------------------------------------------------------
 * Único lugar donde se "arma el rompecabezas". Equivale a
 * `infrastructure/config/UseCaseConfig.java` del backend: aquí —y SOLO aquí—
 * se instancian las clases concretas y se inyectan por constructor.
 *
 * CLEAN ARCHITECTURE aplicada aquí:
 *  - Inyección de Dependencias pura (sin frameworks): construimos el grafo de
 *    objetos manualmente, de afuera hacia adentro.
 *  - DIP materializada: la decisión "mock vs HTTP real" se toma en UN punto.
 *    El núcleo (dominio + caso de uso + vista) jamás conoce esta elección.
 *
 *  👉 PARA CONECTAR LA API REAL: comenta la línea del Mock y descomenta la del
 *     HttpSubscriptionGateway. Nada más cambia en todo el proyecto.
 */

import { FinancialAnalyzer } from "./domain/FinancialAnalyzer.js?v=6";
import { DashboardService } from "./application/DashboardService.js?v=6";
// import { MockSubscriptionGateway } from "./infrastructure/MockSubscriptionGateway.js";
import { HttpSubscriptionGateway } from "./infrastructure/HttpSubscriptionGateway.js?v=6";
import { HttpAuthGateway } from "./infrastructure/HttpAuthGateway.js?v=6";
import { DashboardView } from "./presentation/DashboardView.js?v=6";
import { LoginView } from "./presentation/LoginView.js?v=6";

const BASE_URL = "http://localhost:8081";

/**
 * Arranca el tablero para el correo del usuario que inició sesión.
 * Se llama SOLO después de un login correcto (ver LoginView).
 */
function startDashboard(email) {
  // Pinta los datos de sesión en el encabezado (nombre, correo, avatar).
  const name = sessionStorage.getItem("subscrybe_name") || email.split("@")[0];
  document.getElementById("session-name").textContent = name;
  document.getElementById("session-email").textContent = email;
  document.getElementById("session-avatar").textContent = name.charAt(0);

  // Cerrar sesión: limpia la sesión y vuelve a la pantalla de inicio.
  document.getElementById("logout-btn").addEventListener("click", () => {
    sessionStorage.removeItem("subscrybe_token");
    sessionStorage.removeItem("subscrybe_email");
    sessionStorage.removeItem("subscrybe_name");
    location.reload();
  });

  // 1. Adaptador de salida: API REAL atada al correo del usuario logueado.
  const gateway = new HttpSubscriptionGateway(BASE_URL, email);
  // 2. Servicio de dominio (NÚCLEO).
  const analyzer = new FinancialAnalyzer();
  // 3. Caso de uso (APLICACIÓN) recibe sus dependencias por constructor.
  const dashboardService = new DashboardService(gateway, analyzer);
  // 4. Vista (PRESENTACIÓN) recibe el caso de uso.
  const view = new DashboardView(dashboardService, document);
  view.init();
}

// Puerta de entrada: primero login. Si ya hay sesión guardada, entra directo.
const auth = new HttpAuthGateway(BASE_URL);
const savedEmail = sessionStorage.getItem("subscrybe_email");

if (savedEmail) {
  document.getElementById("login-overlay").style.display = "none";
  document.getElementById("app-root").style.display = "block";
  startDashboard(savedEmail);
} else {
  const login = new LoginView(auth, startDashboard, document);
  login.init();
}
