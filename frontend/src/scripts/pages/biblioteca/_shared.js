const NAV = {
    biblioteca: [
        ["Acervo",        "/src/templates/biblioteca/acervo.html"],
        ["Exemplares",    "/src/templates/biblioteca/exemplares.html"],
        ["Empréstimos",   "/src/templates/biblioteca/emprestimos.html"],
        ["Devoluções",    "/src/templates/biblioteca/devolucoes.html"],
        ["Reservas",      "/src/templates/biblioteca/reservas.html"],
        ["Multas",        "/src/templates/biblioteca/multas.html"],
        ["Configurações", "/src/templates/biblioteca/configuracoes.html"]
    ],
    aluno: [
        ["Consultar acervo", "/src/templates/aluno/biblioteca/consultar.html"],
        ["Meus empréstimos", "/src/templates/aluno/biblioteca/meus-emprestimos.html"],
        ["Minhas reservas",  "/src/templates/aluno/biblioteca/minhas-reservas.html"]
    ]
};

// --- MONTA O CABECALHO E O MENU VIA DOM ---
export function renderHeader(titulo, portal = "biblioteca") {
    const header = document.querySelector("header");
    if (!header) {
        return;
    }
    header.replaceChildren();

    const h1 = document.createElement("h1");
    h1.textContent = titulo;

    const nav = document.createElement("nav");
    nav.className = "nav-biblioteca";
    for (const [label, href] of NAV[portal] || []) {
        const a = document.createElement("a");
        a.href = href;
        a.textContent = label;
        nav.appendChild(a);
    }
    header.append(h1, nav);
}

// --- MENSAGEM DE ERRO EM UM CONTAINER ---
export function showError(el, msg) {
    if (!el) {
        return;
    }
    el.textContent = msg;
    el.className = "msg-error";
    el.hidden = false;
}

// --- MENSAGEM DE SUCESSO EM UM CONTAINER ---
export function showOk(el, msg) {
    if (!el) {
        return;
    }
    el.textContent = msg;
    el.className = "msg-ok";
    el.hidden = false;
}

// --- FORMATA DATA ISO PARA O PADRAO BR ---
export function formatDate(iso) {
    if (!iso) {
        return "-";
    }
    return new Date(iso).toLocaleString("pt-BR");
}

// --- FORMATA VALOR MONETARIO EM BRL ---
export function money(v) {
    if (v == null) {
        return "-";
    }
    return Number(v).toLocaleString("pt-BR", { style: "currency", currency: "BRL" });
}

// --- CRIA UM ELEMENTO DE BADGE DE STATUS ---
export function badge(status) {
    const span = document.createElement("span");
    span.className = `badge ${status}`;
    span.textContent = status;
    return span;
}
