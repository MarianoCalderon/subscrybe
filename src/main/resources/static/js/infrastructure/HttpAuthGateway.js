/**
 * CAPA: INFRAESTRUCTURA (Adaptador de salida)
 * --------------------------------------------------------------------------
 * Habla con los endpoints REALES de autenticación del backend:
 *   - POST /api/auth/login?email=&password=     -> devuelve un JWT (texto)
 *   - POST /api/users/register?name=&email=&password=
 *
 * SRP: su única responsabilidad es traducir llamadas de la app a HTTP y de
 * vuelta. No sabe nada de la UI ni de cómo se guarda la sesión.
 */
export class HttpAuthGateway {
  constructor(baseUrl = "http://localhost:8081") {
    this.baseUrl = baseUrl;
  }

  /**
   * Inicia sesión. Devuelve el JWT si las credenciales son válidas.
   * @throws {Error} con el mensaje del backend si las credenciales fallan.
   */
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
    return body; // JWT
  }

  /**
   * Registra un usuario nuevo.
   * @throws {Error} con el mensaje del backend si el registro falla.
   */
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
