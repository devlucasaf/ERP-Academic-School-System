// --- CLIENTE HTTP GENÉRICO USADO POR TODOS OS REMOTES ---
const API_BASE = '/api';

function getToken() {
    return localStorage.getItem('token');
}

function buildHeaders(extra = {}, isJson = true) {
    const headers = { ...extra };
    if (isJson) headers['Content-Type'] = 'application/json';
    const token = getToken();
    if (token) headers['Authorization'] = `Bearer ${token}`;
    return headers;
}

async function handle(response) {
    if (response.status === 204) return null;
    const text = await response.text();
    const data = text ? JSON.parse(text) : null;
    if (!response.ok) {
        const msg = (data && (data.message || data.error)) || `Erro ${response.status}`;
        throw new Error(msg);
    }
    return data;
}

export const http = {
    get: (path, params) => {
        const qs = params ? '?' + new URLSearchParams(params).toString() : '';
        return fetch(`${API_BASE}${path}${qs}`, { headers: buildHeaders() }).then(handle);
    },
    post: (path, body) =>
        fetch(`${API_BASE}${path}`, {
            method: 'POST',
            headers: buildHeaders(),
            body: body ? JSON.stringify(body) : undefined
        }).then(handle),
    put: (path, body) =>
        fetch(`${API_BASE}${path}`, {
            method: 'PUT',
            headers: buildHeaders(),
            body: body ? JSON.stringify(body) : undefined
        }).then(handle),
    del: (path) =>
        fetch(`${API_BASE}${path}`, {
            method: 'DELETE',
            headers: buildHeaders()
        }).then(handle),
    // --- ENVIO MULTIPART COM CAMPO "dados" JSON + ARQUIVO ---
    postMultipart: (path, dadosObj, arquivo, fileFieldName = 'arquivo', dadosFieldName = 'dados') => {
        const fd = new FormData();
        fd.append(dadosFieldName, new Blob([JSON.stringify(dadosObj)], { type: 'application/json' }));
        if (arquivo) fd.append(fileFieldName, arquivo);
        return fetch(`${API_BASE}${path}`, {
            method: 'POST',
            headers: buildHeaders({}, false),
            body: fd
        }).then(handle);
    },
    putMultipart: (path, dadosObj, arquivo, fileFieldName = 'arquivo', dadosFieldName = 'dados') => {
        const fd = new FormData();
        fd.append(dadosFieldName, new Blob([JSON.stringify(dadosObj)], { type: 'application/json' }));
        if (arquivo) fd.append(fileFieldName, arquivo);
        return fetch(`${API_BASE}${path}`, {
            method: 'PUT',
            headers: buildHeaders({}, false),
            body: fd
        }).then(handle);
    }
};

