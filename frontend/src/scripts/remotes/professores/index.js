// --- REMOTE DE PROFESSORES (ENDPOINTS /api/professores) ---
import { api } from '../../util.js';

const BASE = '/professores';

function query(parametros) {
    const p = new URLSearchParams();
    Object.entries(parametros || {}).forEach(([chave, valor]) => {
        if (valor !== undefined && valor !== null && valor !== '') p.append(chave, valor);
    });
    const texto = p.toString();
    return texto ? `?${texto}` : '';
}

export const professoresApi = {
    listar: (parametros) => api(`${BASE}${query(parametros)}`),
    buscarPorId: (id) => api(`${BASE}/${id}`),
    criar: (dados) => api(BASE, { metodo: 'POST', corpo: dados }),
    atualizar: (id, dados) => api(`${BASE}/${id}`, { metodo: 'PUT', corpo: dados }),
    excluir: (id) => api(`${BASE}/${id}`, { metodo: 'DELETE' })
};

