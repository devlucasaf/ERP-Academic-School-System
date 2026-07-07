// --- COMPONENTES E HELPERS COMPARTILHADOS PELAS TELAS DA BIBLIOTECA ---
export function renderHeader(titulo, portal = 'biblioteca') {
    const links = portal === 'aluno'
        ? [
            ['Consultar acervo', '/src/scripts/pages/aluno/biblioteca/consultar.html'],
            ['Meus empréstimos', '/src/scripts/pages/aluno/biblioteca/meus-emprestimos.html'],
            ['Minhas reservas',  '/src/scripts/pages/aluno/biblioteca/minhas-reservas.html']
          ]
        : [
            ['Acervo',         '/src/scripts/pages/biblioteca/acervo.html'],
            ['Exemplares',     '/src/scripts/pages/biblioteca/exemplares.html'],
            ['Empréstimos',    '/src/scripts/pages/biblioteca/emprestimos.html'],
            ['Devoluções',     '/src/scripts/pages/biblioteca/devolucoes.html'],
            ['Reservas',       '/src/scripts/pages/biblioteca/reservas.html'],
            ['Multas',         '/src/scripts/pages/biblioteca/multas.html'],
            ['Configurações',  '/src/scripts/pages/biblioteca/configuracoes.html']
          ];
    const header = document.querySelector('header');
    if (header) {
        header.innerHTML = `
            <h1>${titulo}</h1>
            <nav class="nav-biblioteca">
                ${links.map(([label, href]) => `<a href="${href}">${label}</a>`).join('')}
            </nav>
        `;
    }
}

export function showError(container, msg) {
    if (!container) return;
    container.innerHTML = `<p class="msg-error">${msg}</p>`;
}

export function showOk(container, msg) {
    if (!container) return;
    container.innerHTML = `<p class="msg-ok">${msg}</p>`;
}

export function formatDate(iso) {
    if (!iso) return '-';
    const d = new Date(iso);
    return d.toLocaleString('pt-BR');
}

export function money(v) {
    if (v == null) return '-';
    return Number(v).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
}

export function badge(status) {
    return `<span class="badge ${status}">${status}</span>`;
}

