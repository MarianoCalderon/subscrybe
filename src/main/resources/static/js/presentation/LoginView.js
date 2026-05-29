export class LoginView {
  constructor(auth, onLogin, doc = document) {
    this.auth = auth;
    this.onLogin = onLogin;
    this.doc = doc;
    this.mode = "login";
  }

  init() {
    this.overlay = this.doc.getElementById("login-overlay");
    this.form = this.doc.getElementById("login-form");
    this.nameRow = this.doc.getElementById("login-name-row");
    this.nameInput = this.doc.getElementById("login-name");
    this.emailInput = this.doc.getElementById("login-email");
    this.passInput = this.doc.getElementById("login-password");
    this.submitBtn = this.doc.getElementById("login-submit");
    this.toggleBtn = this.doc.getElementById("login-toggle");
    this.titleEl = this.doc.getElementById("login-title");
    this.feedback = this.doc.getElementById("login-feedback");

    this.form.addEventListener("submit", (e) => this._onSubmit(e));
    this.toggleBtn.addEventListener("click", () => this._toggleMode());

    this._applyMode();
  }

  _toggleMode() {
    this.mode = this.mode === "login" ? "register" : "login";
    this._setFeedback("");
    this._applyMode();
  }

  _applyMode() {
    const isRegister = this.mode === "register";
    this.titleEl.textContent = isRegister ? "Crear cuenta" : "Iniciar sesión";
    this.submitBtn.textContent = isRegister ? "Registrarme" : "Entrar";
    this.toggleBtn.textContent = isRegister
      ? "¿Ya tienes cuenta? Inicia sesión"
      : "¿No tienes cuenta? Crear una";
    this.nameRow.style.display = isRegister ? "flex" : "none";
    this.nameInput.required = isRegister;
  }

  async _onSubmit(e) {
    e.preventDefault();
    const email = this.emailInput.value.trim();
    const password = this.passInput.value;
    const name = this.nameInput.value.trim();

    this.submitBtn.disabled = true;
    this._setFeedback("");

    try {
      if (this.mode === "register") {
        await this.auth.register(name, email, password);
      }
      const token = await this.auth.login(email, password);
      const displayName = name || email.split("@")[0];
      sessionStorage.setItem("subscrybe_token", token);
      sessionStorage.setItem("subscrybe_email", email);
      sessionStorage.setItem("subscrybe_name", displayName);
      this._enterDashboard(email);
    } catch (err) {
      this._setFeedback(err.message || "No se pudo iniciar sesión.", true);
      this.submitBtn.disabled = false;
    }
  }

  _enterDashboard(email) {
    this.overlay.style.display = "none";
    this.doc.getElementById("app-root").style.display = "block";
    this.onLogin(email);
  }

  _setFeedback(text, isError = false) {
    this.feedback.textContent = text;
    this.feedback.classList.toggle("error", isError);
  }
}
