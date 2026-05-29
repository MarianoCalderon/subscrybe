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

import { FinancialAnalyzer } from "./domain/FinancialAnalyzer.js";
import { DashboardService } from "./application/DashboardService.js";
import { MockSubscriptionGateway } from "./infrastructure/MockSubscriptionGateway.js";
// import { HttpSubscriptionGateway } from "./infrastructure/HttpSubscriptionGateway.js";
import { DashboardView } from "./presentation/DashboardView.js";

// 1. Adaptador de salida (DETALLE intercambiable) ----------------------------
const gateway = new MockSubscriptionGateway();
// const gateway = new HttpSubscriptionGateway("http://localhost:8081");

// 2. Servicio de dominio (NÚCLEO) -------------------------------------------
const analyzer = new FinancialAnalyzer();

// 3. Caso de uso (APLICACIÓN) recibe sus dependencias por constructor --------
const dashboardService = new DashboardService(gateway, analyzer);

// 4. Vista (PRESENTACIÓN) recibe el caso de uso ------------------------------
const view = new DashboardView(dashboardService, document);

view.init();
