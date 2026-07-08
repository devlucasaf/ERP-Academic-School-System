const BASE_API = "/api";
const CHAVE_TOKEN = "token";

// --- WRAPPER DE FETCH QUE INJETA O JWT AUTOMATICAMENTE ---
export async function api(caminho, { metodo = "GET", corpo, cabecalhos = {}, multipart = false } = {}) {
    const opcoes = {
        method: metodo,
        headers: {
            ...cabecalhos
        }
    };

    const token = localStorage.getItem(CHAVE_TOKEN);
    if (token) opcoes.headers.Authorization = `Bearer ${token}`;

    if (corpo !== undefined && corpo !== null) {
        if (multipart) {
            opcoes.body = corpo;
        } else {
            opcoes.headers["Content-Type"] = "application/json";
            opcoes.body = JSON.stringify(corpo);
        }
    }

    const resposta = await fetch(`${BASE_API}${caminho}`, opcoes);
    const texto = await resposta.text();
    const dados = texto ? JSON.parse(texto) : null;

    if (resposta.status === 401) {
        ["token", "refreshToken", "user", "usuarioId"].forEach(chave => localStorage.removeItem(chave));
        if (location.hash !== "#/login") {
            location.hash = "#/login";
        }
        throw new Error((dados && dados.message) || "Sessão expirada. Faça login novamente.");
    }

    if (!resposta.ok) {
        throw new Error((dados && (dados.message || dados.error)) || `Erro ${resposta.status}`);
    }

    return resposta.status === 204 ? null : dados;
}

// --- MÁSCARA DE CPF ---
export function mascararCpf(valor) {
    return (valor || "")
        .replace(/\D/g, "")
        .slice(0, 11)
        .replace(/(\d{3})(\d)/, "$1.$2")
        .replace(/(\d{3})(\d)/, "$1.$2")
        .replace(/(\d{3})(\d{1,2})$/, "$1-$2");
}

// --- MÁSCARA DE TELEFONE ---
export function mascararTelefone(valor) {
    return (valor || "")
        .replace(/\D/g, "")
        .slice(0, 11)
        .replace(/(\d{2})(\d)/, "($1) $2")
        .replace(/(\d{5})(\d)/, "$1-$2");
}

// --- APLICA UMA MÁSCARA A UM CAMPO ENQUANTO O USUÁRIO DIGITA ---
export function aplicarMascara(campo, funcaoMascara) {
    if (!campo) {
        return;
    }
    campo.addEventListener("input", () => {
        campo.value = funcaoMascara(campo.value);
    });
}

// --- FORMATA DATA ISO PARA dd/mm/aaaa ---
export function formatarData(iso) {
    if (!iso) {
        return "-";
    }
    return new Date(iso).toLocaleDateString("pt-BR");
}

// --- FORMATA DATA/HORA ISO PARA dd/mm/aaaa hh:mm ---
export function formatarDataHora(iso) {
    if (!iso) {
        return "-";
    }
    return new Date(iso).toLocaleString("pt-BR");
}

// --- FORMATA VALOR MONETÁRIO EM BRL ---
export function formatarMoeda(valor) {
    if (valor == null) {
        return "-";
    }
    return Number(valor).toLocaleString("pt-BR", {
        style: "currency",
        currency: "BRL"
    });
}

// --- SISTEMA DE NOTIFICAÇÕES ---
let raizNotificacoes;

function garantirRaizNotificacoes() {
    if (!raizNotificacoes) {
        raizNotificacoes = document.createElement("div");
        raizNotificacoes.className = "toast-container";
        document.body.appendChild(raizNotificacoes);
    }
    return raizNotificacoes;
}

// --- EXIBE UMA NOTIFICAÇÃO ---
export function notificar(mensagem, tipo = "info", tempo = 3500) {
    const raiz = garantirRaizNotificacoes();
    const elemento = document.createElement("div");
    elemento.className = `toast toast-${tipo}`;
    elemento.textContent = mensagem;
    raiz.appendChild(elemento);

    requestAnimationFrame(() => elemento.classList.add("show"));
    setTimeout(() => {
        elemento.classList.remove("show");
        setTimeout(() => elemento.remove(), 300);
    }, tempo);
}

