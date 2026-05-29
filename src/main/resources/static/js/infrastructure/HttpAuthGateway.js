export class HttpAuthGateway {
  constructor(baseUrl = "http://localhost:8081") {
    this.baseUrl = baseUrl;
  }

  async login(email, password) {
    const url =
      `${this.baseUrl}/api/auth/login` +
      `?email=${encodeURIComponent(email)}` +
      `&password=${encodeURIComponent(password)}`;

    const res = await fetch(url, { method: "POST" });
    const body = await res.text();
    if (!res.ok) {
      throw new Error(body || "Credenciales inválidas.");
    }
    return body;
  }

  async register(name, email, password) {
    const url =
      `${this.baseUrl}/api/users/register` +
      `?name=${encodeURIComponent(name)}` +
      `&email=${encodeURIComponent(email)}` +
      `&password=${encodeURIComponent(password)}`;

    const res = await fetch(url, { method: "POST" });
    const body = await res.text();
    if (!res.ok) {
      throw new Error(body || "No se pudo registrar el usuario.");
    }
    return body;
  }
}
