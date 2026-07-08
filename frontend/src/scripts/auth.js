import { autenticacaoApi } from "./remotes/auth/index.js";

const CHAVE_TOKEN = "token";
const CHAVE_REFRESH = "refreshToken";
const CHAVE_USUARIO = "user";

// --- MAPA DE REDIRECIONAMENTO PÓS-LOGIN POR PERFIL ---
export const DASHBOARD_POR_PERFIL = {
    ALUNO: "#/aluno/dashboard",
    PROFESSOR: "#/professor/dashboard",
    COORDENADOR: "#/coordenacao/dashboard",
    SECRETARIA: "#/secretaria/dashboard",
    BIBLIOTECARIO: "#/biblioteca/dashboard",
    FINANCEIRO: "#/financeiro/dashboard",
    RESPONSAVEL: "#/responsavel/dashboard",
    ADMIN: "#/admin/dashboard"
};

// --- RESOLVE A ROTA DO DASHBOARD DE UM PERFIL ---
export function dashboardDoPerfil(perfil) {
    return DASHBOARD_POR_PERFIL[perfil] || "#/login";
}

// --- AUTENTICA E GRAVA A SESSÃO ---
export async function autenticar(email, senha) {
    const resposta = await autenticacaoApi.autenticar({
        email,
        senha
    });
    gravarSessao(resposta);
    return resposta.usuario;
}

// --- GRAVA TOKENS E DADOS DO USUÁRIO ---
export function gravarSessao({ token, refreshToken, usuario }) {
    localStorage.setItem(CHAVE_TOKEN, token);
    if (refreshToken) {
        localStorage.setItem(CHAVE_REFRESH, refreshToken);
    }

    if (usuario) {
        localStorage.setItem(CHAVE_USUARIO, JSON.stringify(usuario));
        if (usuario.id) {
            localStorage.setItem("usuarioId", usuario.id);
        }
    }
}

// --- ENCERRA A SESSÃO ---
export function encerrarSessao() {
    [CHAVE_TOKEN, CHAVE_REFRESH, CHAVE_USUARIO, "usuarioId"].forEach(chave => localStorage.removeItem(chave));
}

// --- RETORNA O ACCESS TOKEN ARMAZENADO ---
export function obterToken() {
    return localStorage.getItem(CHAVE_TOKEN);
}

// --- RETORNA O USUÁRIO ARMAZENADO ---
export function obterUsuario() {
    const bruto = localStorage.getItem(CHAVE_USUARIO);
    return bruto ? JSON.parse(bruto) : null;
}

// --- INDICA SE HÁ SESSÃO ATIVA ---
export function estaAutenticado() {
    return !!obterToken();
}

// --- VERIFICA SE O USUÁRIO POSSUI ALGUM DOS PERFIS INFORMADOS ---
export function possuiPerfil(perfis) {
    const usuario = obterUsuario();
    if (!usuario) {
        return false;
    }
    const lista = Array.isArray(perfis) ? perfis : [perfis];
    return lista.includes(usuario.role);
}

