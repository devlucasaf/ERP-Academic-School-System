import { http } from "../http.js";

// --- MULTAS ---
export const multaApi = {
    listar: (params = {}) => http.get("/biblioteca/multas", params),
    pendentesDoUsuario: (usuarioId) => http.get(`/biblioteca/multas/usuario/${usuarioId}/pendentes`),
    pagar: (id) => http.post(`/biblioteca/multas/${id}/pagar`),
    cancelar: (id) => http.post(`/biblioteca/multas/${id}/cancelar`)
};

