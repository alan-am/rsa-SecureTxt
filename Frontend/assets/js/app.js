/* Utilidades UI */
const qs = (s, r = document) => r.querySelector(s);
const qsa = (s, r = document) => [...r.querySelectorAll(s)];
const setJson = (el, obj) => el.textContent = JSON.stringify(obj, null, 2);
const setText = (el, txt) => el.textContent = String(txt ?? "");
const toast = (msg, isError = false) => {
  const el = document.createElement("div");
  el.className = "toast" + (isError ? " danger" : "");
  el.textContent = msg;
  document.body.appendChild(el);
  setTimeout(() => el.classList.add("show"));
  setTimeout(() => { el.classList.remove("show"); el.addEventListener("transitionend", () => el.remove(), { once:true }); }, 1600);
};
// inyecta estilos de toast
(() => {
  const style = document.createElement("style");
  style.textContent = `
  .toast{position:fixed;left:50%;bottom:24px;transform:translate(-50%,16px);
    background:#24284a;color:#e6e8f0;padding:10px 14px;border-radius:12px;
    opacity:0;transition:all .2s ease-in-out;z-index:9999;box-shadow:0 10px 20px rgba(0,0,0,.3)}
  .toast.danger{background:#4a2430}.toast.show{opacity:1;transform:translate(-50%,0)}
  `;
  document.head.appendChild(style);
})();

/* Helper para mostrar SOLO el mensaje de error */
function formatErr(err){
  if (!err) return "Error inesperado";
  if (typeof err === "string") return err;
  return err.message || err.error || "Error inesperado";
}
function showError(el, err){
  setText(el, formatErr(err));
}

/* Heurística sencilla para detectar texto “raro” (no legible) */
function isLikelyWeirdText(s){
  if (!s) return true;
  // Permitimos ASCII imprimible + saltos y caracteres comunes en español:
  const allowed = /[\t\n\r -~¡¿áéíóúüñÁÉÍÓÚÜÑ]/;
  let bad = 0, total = 0;
  for (const ch of s){
    total++;
    if (!allowed.test(ch)) bad++;
  }
  const ratio = bad / Math.max(1,total);

  // Señales fuertes de problema:
  const hasReplacement = s.includes("\uFFFD"); // �
  const hasManyNulls = (s.match(/\u0000/g) || []).length >= 1;
  // Si más del 10% de chars no son “permitidos” o hay señales fuertes → raro
  return ratio > 0.10 || hasReplacement || hasManyNulls;
}

/* Estado de “sesión” (muy simple) */
const Session = {
  get(){ try{ return JSON.parse(localStorage.getItem("rsa_user")||"null"); }catch{ return null; } },
  set(user){ localStorage.setItem("rsa_user", JSON.stringify(user)); },
  clear(){ localStorage.removeItem("rsa_user"); }
};

/* Referencias de vistas */
const views = {
  login: qs("#view-login"),
  home: qs("#view-home"),
  encrypt: qs("#view-encrypt"),
  decrypt: qs("#view-decrypt"),
};
const navSession = qs("#nav-session");
const lblUser = qs("#lbl-user");
const btnLogout = qs("#btn-logout");

/* Salir */
btnLogout.addEventListener("click", () => {
  Session.clear();
  location.reload();
});

/* Tabs login */
qsa(".tab").forEach(btn => {
  btn.addEventListener("click", () => {
    qsa(".tab").forEach(b => b.classList.remove("active"));
    qsa(".tab-panel").forEach(p => p.classList.remove("active"));
    btn.classList.add("active");
    qs(`.tab-panel[data-panel="${btn.dataset.tab}"]`).classList.add("active");
  });
});

/* Formularios de login */
const outLoginId = qs("#out-login-id");
const outLoginCreate = qs("#out-login-create");

qs("#form-login-id").addEventListener("submit", async (e) => {
  e.preventDefault();
  setText(outLoginId, "Verificando…");
  const id = e.target.id.value.trim();
  try{
    const data = await window.Api.getUsuario(id);
    Session.set({ id: data.id, nombre: data.nombre });
    toast(`Bienvenido, ${data.nombre}`);
    boot();
  }catch(err){
    showError(outLoginId, err);   // solo mensaje
  }
});

qs("#form-login-create").addEventListener("submit", async (e) => {
  e.preventDefault();
  setText(outLoginCreate, "Creando…");
  const nombre = e.target.nombre.value.trim();
  try{
    const data = await window.Api.registrarUsuario(nombre);
    Session.set({ id: data.id, nombre: data.nombre });
    toast(`Usuario creado: ${data.nombre}`);
    boot();
  }catch(err){
    showError(outLoginCreate, err);  // solo mensaje
  }
});

/* Navegación principal */
qs("#go-encrypt").addEventListener("click", () => show("encrypt", preloadEncrypt));
qs("#go-decrypt").addEventListener("click", () => show("decrypt", preloadDecrypt));
qsa("[data-back]").forEach(b => b.addEventListener("click", () => show("home")));

/* ENCRIPTAR */
const meId = qs("#me-id");
const selectRecipient = qs("#select-recipient");
const fileEncrypt = qs("#file-encrypt");
const btnPickEncrypt = qs("#btn-pick-encrypt");
const chosenEncrypt = qs("#chosen-encrypt");
const outEncrypt = qs("#out-encrypt");

btnPickEncrypt.addEventListener("click", () => fileEncrypt.click());
fileEncrypt.addEventListener("change", () => {
  chosenEncrypt.textContent = fileEncrypt.files[0]?.name || "";
});

qs("#form-encrypt").addEventListener("submit", async (e) => {
  e.preventDefault();
  outEncrypt.textContent = "Procesando…";

  const sess = Session.get();
  const archivo = fileEncrypt.files[0];
  const receptorId = selectRecipient.value;
  const receptorName = selectRecipient.options[selectRecipient.selectedIndex]?.text || `ID ${receptorId}`;

  // Validación de .txt clara
  const isTxt = !!archivo && /\.txt$/i.test(archivo.name);
  if(!archivo){ setText(outEncrypt, "Selecciona un archivo .txt"); return; }
  if(!isTxt){ setText(outEncrypt, "Sólo se permiten archivos .txt"); return; }
  if(!receptorId){ setText(outEncrypt, "Selecciona un destinatario"); return; }

  try{
    const data = await window.Api.encriptarArchivo({
      archivo,
      emisorId: sess.id,
      receptorId
    });

    // ✅ Mensaje limpio (sin JSON)
    setText(
      outEncrypt,
      `✅ Encriptación exitosa.\nEmisor: ${sess.nombre} (ID ${sess.id})\nReceptor: ${receptorName}`
    );

    // ⬇️ Descarga automática del archivo cifrado usando la respuesta del backend
    const contenido = data?.contenidoCifrado || "";
    if (contenido) {
      const outName = archivo.name.replace(/\.txt$/i, "") + "_encrypted.txt";
      const blob = new Blob([contenido], { type: "text/plain;charset=utf-8" });
      const a = document.createElement("a");
      a.href = URL.createObjectURL(blob);
      a.download = outName;
      document.body.appendChild(a);
      a.click();
      URL.revokeObjectURL(a.href);
      a.remove();
      toast("Archivo encriptado descargado");
    } else {
      // Si por alguna razón el backend no envía contenido
      toast("Encriptado OK, pero no se recibió el contenido para descargar", true);
    }
  }catch(err){
    showError(outEncrypt, err);   // solo mensaje de error
  }
});


async function preloadEncrypt(){
  const sess = Session.get();
  meId.value = `${sess.nombre} (ID ${sess.id})`;

  // Cargar usuarios y poblar destinatarios (excluirme)
  selectRecipient.innerHTML = `<option value="" disabled selected>Cargando…</option>`;
  try{
    const users = await window.Api.listarUsuarios();
    const options = users
      .filter(u => u.id !== sess.id)
      .map(u => `<option value="${u.id}">${u.nombre} (ID ${u.id})</option>`);
    selectRecipient.innerHTML = `<option value="" disabled selected>Selecciona un usuario…</option>${options.join("")}`;
  }catch(err){
    selectRecipient.innerHTML = `<option value="" disabled selected>Error al cargar</option>`;
    showError(outEncrypt, err);   // solo mensaje
  }
  // reset UI
  fileEncrypt.value = "";
  chosenEncrypt.textContent = "";
  outEncrypt.textContent = "";
}

/* DESENCRIPTAR */
const meIdDec = qs("#me-id-dec");
const fileDecrypt = qs("#file-decrypt");
const btnPickDecrypt = qs("#btn-pick-decrypt");
const chosenDecrypt = qs("#chosen-decrypt");
const outDecrypt = qs("#out-decrypt");
const decryptedContent = qs("#decrypted-content");
const btnDownloadDecrypted = qs("#btn-download-decrypted");
let lastDecrypted = { name:"", content:"" };

btnPickDecrypt.addEventListener("click", () => fileDecrypt.click());
fileDecrypt.addEventListener("change", () => {
  chosenDecrypt.textContent = fileDecrypt.files[0]?.name || "";
});

qs("#form-decrypt").addEventListener("submit", async (e) => {
  e.preventDefault();
  outDecrypt.textContent = "Procesando…";
  decryptedContent.value = "";
  btnDownloadDecrypted.disabled = true;

  const sess = Session.get();
  const file = fileDecrypt.files[0];

  if(!file){ setText(outDecrypt, "Selecciona el archivo cifrado (.txt)"); return; }
  if(!/\.txt$/i.test(file.name)){ setText(outDecrypt, "Sólo se permiten archivos .txt"); return; }

  try{
    const contenidoCifrado = await file.text();
    const nombreArchivo = file.name.replace(/_encrypted\.txt$/i,".txt"); // mejor esfuerzo
    const data = await window.Api.desencriptar({
      contenidoCifrado,
      nombreArchivo,
      usuarioId: sess.id
    });

    // Analizamos el texto plano para mostrar solo mensajes
    const plain = data.contenidoDescifrado || "";
    decryptedContent.value = plain;
    lastDecrypted.name = data.nombreArchivo || nombreArchivo || "archivo.txt";
    lastDecrypted.content = plain;
    btnDownloadDecrypted.disabled = plain.length === 0;

    if (isLikelyWeirdText(plain)) {
      setText(outDecrypt, "⚠️ Posible problema de desencriptación, puede ser que el archivo no es para ti.");
      toast("Revisa el destinatario", true);
    } else {
      setText(outDecrypt, "✅ Desencriptación exitosa.");
      toast("Archivo desencriptado");
    }
  }catch(err){
    showError(outDecrypt, err);  // solo mensaje
  }
});

btnDownloadDecrypted.addEventListener("click", () => {
  if(!lastDecrypted.content) return;
  const blob = new Blob([lastDecrypted.content], { type: "text/plain;charset=utf-8" });
  const a = document.createElement("a");
  a.href = URL.createObjectURL(blob);
  a.download = lastDecrypted.name || "archivo.txt";
  document.body.appendChild(a);
  a.click();
  URL.revokeObjectURL(a.href);
  a.remove();
});

/* Router de vistas */
function show(name, hook){
  Object.entries(views).forEach(([k,el]) => el.classList.toggle("hidden", k !== name));
  if(hook) hook();
}

async function preloadDecrypt(){
  const sess = Session.get();
  meIdDec.value = `${sess.nombre} (ID ${sess.id})`;

  // resetear UI
  fileDecrypt.value = "";
  chosenDecrypt.textContent = "";
  outDecrypt.textContent = "";
  decryptedContent.value = "";
  btnDownloadDecrypted.disabled = true;
}

/* Boot */
function boot(){
  const sess = Session.get();
  const logo = qs("#logo"); // referencia al logo

  if(!sess){
    navSession.classList.add("hidden");
    show("login");
    if(logo) logo.classList.remove("hidden"); // logo visible en login
    return;
  }
  lblUser.textContent = `Conectado como: ${sess.nombre} (ID ${sess.id})`;
  navSession.classList.remove("hidden");
  show("home");
}

boot();
