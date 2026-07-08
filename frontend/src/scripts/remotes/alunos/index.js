// --- REMOTE DE ALUNOS (ENDPOINTS /api/alunos) ---
import { api } from '../../util.js';

const BASE = '/alunos';

// --- MONTA A QUERY STRING IGNORANDO VALORES VAZIOS ---
function query(parametros) {
    const p = new URLSearchParams();
    Object.entries(parametros || {}).forEach(([chave, valor]) => {
        if (valor !== undefined && valor !== null && valor !== '') p.append(chave, valor);
    });
    const texto = p.toString();
    return texto ? `?${texto}` : '';
}

export const alunosApi = {
    listar: (parametros) => api(`${BASE}${query(parametros)}`),
    buscarPorId: (id) => api(`${BASE}/${id}`),
    buscarPorMatricula: (ra) => api(`${BASE}/matricula/${encodeURIComponent(ra)}`),
    criar: (dados) => api(BASE, { metodo: 'POST', corpo: dados }),
    atualizar: (id, dados) => api(`${BASE}/${id}`, { metodo: 'PUT', corpo: dados }),
    excluir: (id) => api(`${BASE}/${id}`, { metodo: 'DELETE' })
};

