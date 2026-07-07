import { http } from '../http.js';

// --- REMOTE: EXEMPLARES ---
export const exemplarApi = {
    listarPorLivro: (livroId, params = {}) => http.get(`/biblioteca/exemplares/livro/${livroId}`, params),
    buscarPorId: (id) => http.get(`/biblioteca/exemplares/${id}`),
    buscarPorCodigo: (codigo) => http.get(`/biblioteca/exemplares/codigo/${encodeURIComponent(codigo)}`),
    gerarCodigoBarras: () => http.get('/biblioteca/exemplares/gerar-codigo-barras'),
    criar: (dto) => http.post('/biblioteca/exemplares', dto),
    atualizar: (id, dto, status) => http.put(`/biblioteca/exemplares/${id}${status ? `?status=${status}` : ''}`, dto),
    deletar: (id) => http.del(`/biblioteca/exemplares/${id}`)
};

