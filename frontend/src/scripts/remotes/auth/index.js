// --- REMOTE DE AUTENTICAÇÃO (ENDPOINTS /api/auth/*) ---
import { api } from '../../util.js';

export const authApi = {
    // --- AUTENTICA E RETORNA { token, refreshToken, usuario } ---
    login: (credenciais) => api('/auth/login', { method: 'POST', body: credenciais }),

    // --- REGISTRA UM NOVO USUÁRIO (REQUER ADMIN) ---
    register: (dados) => api('/auth/register', { method: 'POST', body: dados }),

    // --- RETORNA OS DADOS DO USUÁRIO AUTENTICADO ---
    me: () => api('/auth/me'),

    // --- RENOVA O ACCESS TOKEN A PARTIR DO REFRESH TOKEN ---
    refresh: (refreshToken) => api('/auth/refresh', { method: 'POST', body: { refreshToken } })
};

