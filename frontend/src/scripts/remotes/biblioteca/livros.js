import { http } from "../http.js";

// --- LIVROS ---
export const livroApi = {
    buscar: (params = {}) => http.get("/biblioteca/livros", params),
    buscarPorId: (id) => http.get(`/biblioteca/livros/${id}`),
    criar: (dados, capa) => http.postMultipart("/biblioteca/livros", dados, capa, "capa"),
    atualizar: (id, dados, capa) => http.putMultipart(`/biblioteca/livros/${id}`, dados, capa, "capa"),
    deletar: (id) => http.del(`/biblioteca/livros/${id}`)
};

