// --- CAMADA DE AUTENTICAÇÃO E SESSÃO (ARMAZENAMENTO EM localStorage) ---
import { authApi } from './remotes/auth/index.js';

const TOKEN_KEY = 'token';
const REFRESH_KEY = 'refreshToken';
const USER_KEY = 'user';

// --- MAPA DE REDIRECIONAMENTO PÓS-LOGIN POR ROLE ---
export const DASHBOARD_BY_ROLE = {
    ALUNO: '#/aluno/dashboard',
    PROFESSOR: '#/professor/dashboard',
    COORDENADOR: '#/coordenacao/dashboard',
    SECRETARIA: '#/secretaria/dashboard',
    BIBLIOTECARIO: '#/biblioteca/dashboard',
    FINANCEIRO: '#/financeiro/dashboard',
    RESPONSAVEL: '#/responsavel/dashboard',
    ADMIN: '#/admin/dashboard'
};

// --- RESOLVE A ROTA DO DASHBOARD DE UMA ROLE ---
export function dashboardForRole(role) {
    return DASHBOARD_BY_ROLE[role] || '#/login';
}

// --- AUTENTICA E GRAVA A SESSÃO; RETORNA O USUÁRIO ---
export async function login(email, senha) {
    const resposta = await authApi.login({ email, senha });
    setSession(resposta);
    return resposta.usuario;
}

// --- GRAVA TOKENS E DADOS DO USUÁRIO ---
export function setSession({ token, refreshToken, usuario }) {
    localStorage.setItem(TOKEN_KEY, token);
    if (refreshToken) localStorage.setItem(REFRESH_KEY, refreshToken);
    if (usuario) {
        localStorage.setItem(USER_KEY, JSON.stringify(usuario));
        // --- MANTÉM COMPATIBILIDADE COM TELAS QUE LEEM 'usuarioId' ---
        if (usuario.id) localStorage.setItem('usuarioId', usuario.id);
    }
}

// --- ENCERRA A SESSÃO ---
export function logout() {
    [TOKEN_KEY, REFRESH_KEY, USER_KEY, 'usuarioId'].forEach(k => localStorage.removeItem(k));
}

// --- RETORNA O ACCESS TOKEN ARMAZENADO ---
export function getToken() {
    return localStorage.getItem(TOKEN_KEY);
}

// --- RETORNA O USUÁRIO ARMAZENADO (OU null) ---
export function getUser() {
    const raw = localStorage.getItem(USER_KEY);
    return raw ? JSON.parse(raw) : null;
}

// --- INDICA SE HÁ SESSÃO ATIVA ---
export function isAuthenticated() {
    return !!getToken();
}

// --- VERIFICA SE O USUÁRIO POSSUI ALGUMA DAS ROLES INFORMADAS ---
export function hasRole(roles) {
    const usuario = getUser();
    if (!usuario) return false;
    const lista = Array.isArray(roles) ? roles : [roles];
    return lista.includes(usuario.role);
}

