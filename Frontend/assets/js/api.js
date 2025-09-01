// Configura la URL base de tu API (si backend y frontend NO están en el mismo host/puerto).
const BASE_URL = "http://localhost:8080";
//const BASE_URL = "https://rsa-securetxt-production.up.railway.app";

// Helpers comunes
async function handleJson(res, okStatus = 200) {
  let data, raw = "";
  try { data = await res.json(); }
  catch {
    try { raw = await res.text(); } catch {}
    throw { status: res.status, message: "Respuesta no JSON", raw };
  }
  if (res.status === okStatus || (res.ok && okStatus === 200)) return data;
  throw {
    status: data.status ?? res.status,
    error: data.error ?? "Error",
    message: data.message ?? "Error inesperado",
    path: data.path
  };
}

// API pública (expuesta en window.Api)
const Api = {
  async registrarUsuario(nombre) {
    const res = await fetch(`${BASE_URL}/api/usuarios/registrar`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ nombre })
    });
    return handleJson(res, 201);
  },
  async getUsuario(id) {
    const res = await fetch(`${BASE_URL}/api/usuarios/${encodeURIComponent(id)}`);
    return handleJson(res, 200);
  },
  async listarUsuarios() {
    const res = await fetch(`${BASE_URL}/api/usuarios`);
    return handleJson(res, 200);
  },
  async encriptarArchivo({ archivo, emisorId, receptorId }) {
    const form = new FormData();
    form.append("archivo", archivo);
    form.append("emisorId", String(emisorId));
    form.append("receptorId", String(receptorId));
    const res = await fetch(`${BASE_URL}/api/rsa/encriptar`, { method: "POST", body: form });
    return handleJson(res, 200);
  },
  async desencriptar({ contenidoCifrado, nombreArchivo, usuarioId }) {
    const params = new URLSearchParams();
    params.set("contenidoCifrado", contenidoCifrado);
    params.set("nombreArchivo", nombreArchivo);
    params.set("usuarioId", String(usuarioId));
    const res = await fetch(`${BASE_URL}/api/rsa/desencriptar`, {
      method: "POST",
      headers: { "Content-Type": "application/x-www-form-urlencoded" },
      body: params.toString()
    });
    return handleJson(res, 200);
  }
};

window.Api = Api;
