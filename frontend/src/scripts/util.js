// --- UTILITÁRIOS GERAIS DA SPA (API, MÁSCARAS, DATAS, TOASTS) ---

const API_BASE = '/api';
const TOKEN_KEY = 'token';

// --- WRAPPER DE FETCH QUE INJETA O JWT AUTOMATICAMENTE ---
export async function api(path, { method = 'GET', body, headers = {}, multipart = false } = {}) {
    const opts = { method, headers: { ...headers } };

    // --- INJETA O TOKEN, SE HOUVER ---
    const token = localStorage.getItem(TOKEN_KEY);
    if (token) opts.headers.Authorization = `Bearer ${token}`;

    // --- MONTA O CORPO (JSON OU MULTIPART) ---
    if (body !== undefined && body !== null) {
        if (multipart) {
            opts.body = body;
        } else {
            opts.headers['Content-Type'] = 'application/json';
            opts.body = JSON.stringify(body);
        }
    }

    const res = await fetch(`${API_BASE}${path}`, opts);
    const text = await res.text();
    const data = text ? JSON.parse(text) : null;

    // --- SESSÃO EXPIRADA / NÃO AUTENTICADO ---
    if (res.status === 401) {
        ['token', 'refreshToken', 'user', 'usuarioId'].forEach(k => localStorage.removeItem(k));
        if (location.hash !== '#/login') location.hash = '#/login';
        throw new Error((data && data.message) || 'Sessão expirada. Faça login novamente.');
    }

    if (!res.ok) {
        throw new Error((data && (data.message || data.error)) || `Erro ${res.status}`);
    }

    return res.status === 204 ? null : data;
}

// --- MÁSCARA DE CPF (000.000.000-00) ---
export function maskCPF(valor) {
    return (valor || '')
        .replace(/\D/g, '')
        .slice(0, 11)
        .replace(/(\d{3})(\d)/, '$1.$2')
        .replace(/(\d{3})(\d)/, '$1.$2')
        .replace(/(\d{3})(\d{1,2})$/, '$1-$2');
}

// --- MÁSCARA DE TELEFONE ((00) 00000-0000) ---
export function maskPhone(valor) {
    return (valor || '')
        .replace(/\D/g, '')
        .slice(0, 11)
        .replace(/(\d{2})(\d)/, '($1) $2')
        .replace(/(\d{5})(\d)/, '$1-$2');
}

// --- APLICA UMA MÁSCARA A UM INPUT ENQUANTO O USUÁRIO DIGITA ---
export function bindMask(input, maskFn) {
    if (!input) return;
    input.addEventListener('input', () => { input.value = maskFn(input.value); });
}

// --- FORMATA DATA ISO PARA dd/mm/aaaa ---
export function formatDate(iso) {
    if (!iso) return '-';
    return new Date(iso).toLocaleDateString('pt-BR');
}

// --- FORMATA DATA/HORA ISO PARA dd/mm/aaaa hh:mm ---
export function formatDateTime(iso) {
    if (!iso) return '-';
    return new Date(iso).toLocaleString('pt-BR');
}

// --- FORMATA VALOR MONETÁRIO EM BRL ---
export function money(v) {
    if (v == null) return '-';
    return Number(v).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
}

// --- SISTEMA DE TOASTS ---
let toastRoot;

function ensureToastRoot() {
    if (!toastRoot) {
        toastRoot = document.createElement('div');
        toastRoot.className = 'toast-container';
        document.body.appendChild(toastRoot);
    }
    return toastRoot;
}

// --- EXIBE UM TOAST (type: info | success | error | warning) ---
export function toast(message, type = 'info', timeout = 3500) {
    const root = ensureToastRoot();
    const el = document.createElement('div');
    el.className = `toast toast-${type}`;
    el.textContent = message;
    root.appendChild(el);

    requestAnimationFrame(() => el.classList.add('show'));
    setTimeout(() => {
        el.classList.remove('show');
        setTimeout(() => el.remove(), 300);
    }, timeout);
}

