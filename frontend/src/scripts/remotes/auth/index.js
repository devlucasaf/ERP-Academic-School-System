import { api } from "../../util.js";

export const autenticacaoApi = {
    // --- AUTENTICA E RETORNA ---
    autenticar: (credenciais) => api("/auth/login", {
        metodo: "POST",
        corpo: credenciais
    }),

    // --- REGISTRA UM NOVO USUÁRIO ---
    registrar: (dados) => api("/auth/register", {
        metodo: "POST",
        corpo: dados
    }),

    // --- RETORNA OS DADOS DO USUÁRIO AUTENTICADO ---
    usuarioLogado: () => api("/auth/me"),

    // --- RENOVA O ACCESS TOKEN A PARTIR DO REFRESH TOKEN ---
    renovar: (refreshToken) => api("/auth/refresh", {
        metodo: "POST",
        corpo: {
            refreshToken
        }
    })
};

