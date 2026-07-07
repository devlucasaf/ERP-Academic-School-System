import { http } from '../http.js';

// --- REMOTE: EMPRÉSTIMOS ---
export const emprestimoApi = {
    registrar: (dto) => http.post('/biblioteca/emprestimos', dto),
    devolver: (id) => http.post(`/biblioteca/emprestimos/${id}/devolver`),
    renovar: (id) => http.post(`/biblioteca/emprestimos/${id}/renovar`),
    buscarPorId: (id) => http.get(`/biblioteca/emprestimos/${id}`),
    listarPorUsuario: (usuarioId, params = {}) => http.get(`/biblioteca/emprestimos/usuario/${usuarioId}`, params)
};

