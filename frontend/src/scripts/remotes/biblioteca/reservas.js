import { http } from '../http.js';

// --- REMOTE: RESERVAS ---
export const reservaApi = {
    reservar: (dto) => http.post('/biblioteca/reservas', dto),
    cancelar: (id) => http.del(`/biblioteca/reservas/${id}`),
    filaDoLivro: (livroId) => http.get(`/biblioteca/reservas/livro/${livroId}/fila`),
    doUsuario: (usuarioId) => http.get(`/biblioteca/reservas/usuario/${usuarioId}`)
};

