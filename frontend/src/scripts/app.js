// --- ROUTER POR HASH COM GUARDA POR ROLE, LAYOUT E TEMA ---
import {
    isAuthenticated,
    getUser,
    hasRole,
    dashboardForRole,
    logout
} from './auth.js';
import { toast } from './util.js';

// --- CARREGA OS TEMPLATES HTML COMO TEXTO (FUNCIONA EM DEV E BUILD) ---
const TEMPLATES = import.meta.glob('/src/templates/{shared,auth,dashboards}/**/*.html', {
    query: '?raw',
    import: 'default'
});

async function carregarTemplate(caminho) {
    const loader = TEMPLATES[`/src/templates/${caminho}`];
    if (!loader) throw new Error(`Template não encontrado: ${caminho}`);
    return loader();
}

// --- TABELA DE ROTAS ---
// public: acessível sem login | roles: papéis autorizados | layout: 'app' (padrão) ou 'blank'
const ROUTES = {
    '#/login': {
        template: 'auth/login.html',
        module: () => import('./pages/auth/login.js'),
        public: true,
        layout: 'blank',
        title: 'Entrar'
    },
    '#/aluno/dashboard': {
        template: 'dashboards/dashboard.html',
        module: () => import('./pages/aluno/dashboard.js'),
        roles: ['ALUNO'],
        title: 'Painel do Aluno'
    },
    '#/professor/dashboard': {
        template: 'dashboards/dashboard.html',
        module: () => import('./pages/professor/dashboard.js'),
        roles: ['PROFESSOR'],
        title: 'Painel do Professor'
    },
    '#/coordenacao/dashboard': {
        template: 'dashboards/dashboard.html',
        module: () => import('./pages/coordenacao/dashboard.js'),
        roles: ['COORDENADOR'],
        title: 'Painel da Coordenação'
    },
    '#/secretaria/dashboard': {
        template: 'dashboards/dashboard.html',
        module: () => import('./pages/secretaria/dashboard.js'),
        roles: ['SECRETARIA'],
        title: 'Painel da Secretaria'
    },
    '#/biblioteca/dashboard': {
        template: 'dashboards/dashboard.html',
        module: () => import('./pages/biblioteca/dashboard.js'),
        roles: ['BIBLIOTECARIO'],
        title: 'Painel da Biblioteca'
    },
    '#/financeiro/dashboard': {
        template: 'dashboards/dashboard.html',
        module: () => import('./pages/financeiro/dashboard.js'),
        roles: ['FINANCEIRO'],
        title: 'Painel Financeiro'
    },
    '#/responsavel/dashboard': {
        template: 'dashboards/dashboard.html',
        module: () => import('./pages/responsavel/dashboard.js'),
        roles: ['RESPONSAVEL'],
        title: 'Painel do Responsável'
    },
    '#/admin/dashboard': {
        template: 'dashboards/dashboard.html',
        module: () => import('./pages/admin/dashboard.js'),
        roles: ['ADMIN'],
        title: 'Painel do Administrador'
    }
};

// ============================================================
// --- TEMA (CLARO/ESCURO) ---
// ============================================================
const THEME_KEY = 'theme';

function aplicarTemaSalvo() {
    const tema = localStorage.getItem(THEME_KEY) || 'light';
    document.documentElement.setAttribute('data-theme', tema);
}

function alternarTema() {
    const atual = document.documentElement.getAttribute('data-theme') === 'dark' ? 'dark' : 'light';
    const novo = atual === 'dark' ? 'light' : 'dark';
    document.documentElement.setAttribute('data-theme', novo);
    localStorage.setItem(THEME_KEY, novo);
}

// ============================================================
// --- LAYOUT DA APLICAÇÃO (SIDEBAR + HEADER) ---
// ============================================================
let layoutMontado = false;

async function garantirLayout() {
    const app = document.getElementById('app');

    if (!layoutMontado || !document.getElementById('content')) {
        app.className = 'app-shell';
        app.innerHTML = await carregarTemplate('shared/shell.html');
        document.getElementById('sidebar').innerHTML = await carregarTemplate('shared/sidebar.html');
        document.getElementById('topbar').innerHTML = await carregarTemplate('shared/header.html');
        ligarEventosHeader();
        layoutMontado = true;
    }

    atualizarInfoUsuario();
    filtrarMenuPorRole();
}

// --- LIGA OS BOTÕES DO HEADER (TEMA, SAIR, MENU) ---
function ligarEventosHeader() {
    document.getElementById('btnTheme')?.addEventListener('click', alternarTema);
    document.getElementById('btnLogout')?.addEventListener('click', () => {
        logout();
        location.hash = '#/login';
    });
    document.getElementById('btnSidebarToggle')?.addEventListener('click', () => {
        document.getElementById('app').classList.toggle('sidebar-open');
    });
}

// --- PREENCHE O NOME DO USUÁRIO NO HEADER ---
function atualizarInfoUsuario() {
    const usuario = getUser();
    document.querySelectorAll('[data-user-nome]').forEach(el => {
        el.textContent = usuario?.nome || '';
    });
}

// --- EXIBE APENAS OS ITENS DE MENU PERMITIDOS PARA A ROLE ATUAL ---
function filtrarMenuPorRole() {
    const usuario = getUser();
    const role = usuario?.role;
    document.querySelectorAll('[data-roles]').forEach(el => {
        const permitidas = el.getAttribute('data-roles').split(',').map(r => r.trim());
        el.hidden = !role || !permitidas.includes(role);
    });
}

// --- DESTACA O LINK ATIVO E ATUALIZA O TÍTULO DA PÁGINA ---
function marcarRotaAtiva(hash, titulo) {
    document.querySelectorAll('.sidebar-nav .nav-link').forEach(a => {
        a.classList.toggle('active', a.getAttribute('href') === hash);
    });
    const tituloEl = document.querySelector('[data-page-title]');
    if (tituloEl) tituloEl.textContent = titulo || '';
}

// ============================================================
// --- RENDERIZAÇÃO DA ROTA ATUAL ---
// ============================================================
async function render() {
    const hash = location.hash || '#/';

    // --- RAIZ: REDIRECIONA CONFORME AUTENTICAÇÃO ---
    if (hash === '#/' || hash === '') {
        location.hash = isAuthenticated() ? dashboardForRole(getUser().role) : '#/login';
        return;
    }

    const route = ROUTES[hash];

    // --- ROTA INEXISTENTE ---
    if (!route) {
        location.hash = isAuthenticated() ? dashboardForRole(getUser().role) : '#/login';
        return;
    }

    // --- GUARDA: EXIGE AUTENTICAÇÃO ---
    if (!route.public && !isAuthenticated()) {
        location.hash = '#/login';
        return;
    }

    // --- GUARDA: JÁ LOGADO NÃO VÊ O LOGIN ---
    if (route.public && hash === '#/login' && isAuthenticated()) {
        location.hash = dashboardForRole(getUser().role);
        return;
    }

    // --- GUARDA: EXIGE ROLE ESPECÍFICA ---
    if (route.roles && !hasRole(route.roles)) {
        toast('Você não tem permissão para acessar esta área.', 'error');
        location.hash = dashboardForRole(getUser()?.role);
        return;
    }

    const app = document.getElementById('app');

    if (route.layout === 'blank') {
        // --- LAYOUT SEM SIDEBAR (LOGIN) ---
        layoutMontado = false;
        app.className = 'auth-screen';
        app.innerHTML = await carregarTemplate(route.template);
        const mod = await route.module();
        mod.mount?.(app, { route });
    } else {
        // --- LAYOUT COMPLETO ---
        await garantirLayout();
        const content = document.getElementById('content');
        content.innerHTML = await carregarTemplate(route.template);
        marcarRotaAtiva(hash, route.title);
        document.getElementById('app').classList.remove('sidebar-open');
        const mod = await route.module();
        mod.mount?.(content, { route });
    }
}

// ============================================================
// --- INICIALIZAÇÃO ---
// ============================================================
aplicarTemaSalvo();
window.addEventListener('hashchange', render);
window.addEventListener('DOMContentLoaded', render);

