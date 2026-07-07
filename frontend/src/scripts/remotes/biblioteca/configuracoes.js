import { http } from '../http.js';

// --- REMOTE: CONFIGURAÇÕES DA BIBLIOTECA ---
export const configuracaoBibliotecaApi = {
    obter: () => http.get('/biblioteca/configuracoes'),
    atualizar: (dto) => http.put('/biblioteca/configuracoes', dto)
};

