import { FinancialAnalyzer } from "./domain/FinancialAnalyzer.js?v=7";
import { DashboardService } from "./application/DashboardService.js?v=7";
import { HttpSubscriptionGateway } from "./infrastructure/HttpSubscriptionGateway.js?v=7";
import { HttpAuthGateway } from "./infrastructure/HttpAuthGateway.js?v=7";
import { DashboardView } from "./presentation/DashboardView.js?v=7";
import { LoginView } from "./presentation/LoginView.js?v=7";

const BASE_URL = "http://localhost:8081";

function startDashboard(email) {
  const name = sessionStorage.getItem("subscrybe_name") || email.split("@")[0];
  document.getElementById("session-name").textContent = name;
  document.getElementById("session-email").textContent = email;
  document.getElementById("session-avatar").textContent = name.charAt(0);

  document.getElementById("logout-btn").addEventListener("click", () => {
    sessionStorage.removeItem("subscrybe_token");
    sessionStorage.removeItem("subscrybe_email");
    sessionStorage.removeItem("subscrybe_name");
    location.reload();
  });

  const gateway = new HttpSubscriptionGateway(BASE_URL, email);
  const analyzer = new FinancialAnalyzer();
  const dashboardService = new DashboardService(gateway, analyzer);
  const view = new DashboardView(dashboardService, document);
  view.init();
}

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
