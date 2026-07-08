import {
    estaAutenticado,
    obterUsuario,
    possuiPerfil,
    dashboardDoPerfil,
    encerrarSessao
} from "./auth.js";
import { notificar } from "./util.js";

const MODELOS = import.meta.glob("/src/templates/{shared,auth,dashboards,secretaria}/**/*.html", {
    query: "?raw",
    import: "default"
});

async function carregarModelo(caminho) {
    const carregador = MODELOS[`/src/templates/${caminho}`];
    if (!carregador) {
        throw new Error(`Modelo não encontrado: ${caminho}`);
    }
    return carregador();
}

// --- TABELA DE ROTAS ---
const ROTAS = {
    "#/login": {
        modelo: "auth/login.html",
        modulo: () => import("./pages/auth/login.js"),
        publico: true,
        modo: "limpo",
        titulo: "Entrar"
    },

    "#/aluno/dashboard": {
        modelo: "dashboards/dashboard.html",
        modulo: () => import("./pages/aluno/dashboard.js"),
        perfis: ["ALUNO"],
        titulo: "Painel do Aluno"
    },

    "#/professor/dashboard": {
        modelo: "dashboards/dashboard.html",
        modulo: () => import("./pages/professor/dashboard.js"),
        perfis: ["PROFESSOR"],
        titulo: "Painel do Professor"
    },

    "#/coordenacao/dashboard": {
        modelo: "dashboards/dashboard.html",
        modulo: () => import("./pages/coordenacao/dashboard.js"),
        perfis: ["COORDENADOR"],
        titulo: "Painel da Coordenação"
    },

    "#/secretaria/dashboard": {
        modelo: "dashboards/dashboard.html",
        modulo: () => import("./pages/secretaria/dashboard.js"),
        perfis: ["SECRETARIA"],
        titulo: "Painel da Secretaria"
    },

    "#/biblioteca/dashboard": {
        modelo: "dashboards/dashboard.html",
        modulo: () => import("./pages/biblioteca/dashboard.js"),
        perfis: ["BIBLIOTECARIO"],
        titulo: "Painel da Biblioteca"
    },

    "#/financeiro/dashboard": {
        modelo: "dashboards/dashboard.html",
        modulo: () => import("./pages/financeiro/dashboard.js"),
        perfis: ["FINANCEIRO"],
        titulo: "Painel Financeiro"
    },

    "#/responsavel/dashboard": {
        modelo: "dashboards/dashboard.html",
        modulo: () => import("./pages/responsavel/dashboard.js"),
        perfis: ["RESPONSAVEL"],
        titulo: "Painel do Responsável"
    },

    "#/admin/dashboard": {
        modelo: "dashboards/dashboard.html",
        modulo: () => import("./pages/admin/dashboard.js"),
        perfis: ["ADMIN"],
        titulo: "Painel do Administrador"
    },

    // --- CADASTROS DA SECRETARIA ---
    "#/secretaria/alunos": {
        modelo: "secretaria/alunos.html",
        modulo: () => import("./pages/secretaria/alunos.js"),
        perfis: ["SECRETARIA", "COORDENADOR", "ADMIN"],
        titulo: "Alunos"
    },
    "#/secretaria/professores": {
        modelo: "secretaria/professores.html",
        modulo: () => import("./pages/secretaria/professores.js"),
        perfis: ["SECRETARIA", "COORDENADOR", "ADMIN"],
        titulo: "Professores"
    },
    "#/secretaria/responsaveis": {
        modelo: "secretaria/responsaveis.html",
        modulo: () => import("./pages/secretaria/responsaveis.js"),
        perfis: ["SECRETARIA", "COORDENADOR", "ADMIN"],
        titulo: "Responsáveis"
    },
    "#/secretaria/funcionarios": {
        modelo: "secretaria/funcionarios.html",
        modulo: () => import("./pages/secretaria/funcionarios.js"),
        perfis: ["SECRETARIA", "COORDENADOR", "ADMIN"],
        titulo: "Funcionários"
    }
};

// --- TEMA ---
const CHAVE_TEMA = "theme";

function aplicarTemaSalvo() {
    const tema = localStorage.getItem(CHAVE_TEMA) || "light";
    document.documentElement.setAttribute("data-theme", tema);
}

function alternarTema() {
    const atual = document.documentElement.getAttribute("data-theme") === "dark" ? "dark" : "light";
    const novo = atual === "dark" ? "light" : "dark";
    document.documentElement.setAttribute("data-theme", novo);
    localStorage.setItem(CHAVE_TEMA, novo);
}

// --- LAYOUT DA APLICAÇÃO ---
let layoutMontado = false;

async function garantirLayout() {
    const app = document.getElementById("app");

    if (!layoutMontado || !document.getElementById("conteudo")) {
        app.className = "app-shell";
        app.innerHTML = await carregarModelo("shared/shell.html");
        document.getElementById("menuLateral").innerHTML = await carregarModelo("shared/sidebar.html");
        document.getElementById("cabecalho").innerHTML = await carregarModelo("shared/header.html");
        ligarEventosCabecalho();
        layoutMontado = true;
    }

    atualizarInfoUsuario();
    filtrarMenuPorPerfil();
}

// --- LIGA OS BOTÕES DO CABEÇALHO ---
function ligarEventosCabecalho() {
    document.getElementById("btnTema")?.addEventListener("click", alternarTema);
    document.getElementById("btnSair")?.addEventListener("click", () => {
        encerrarSessao();
        location.hash = "#/login";
    });
    document.getElementById("btnAlternarMenu")?.addEventListener("click", () => {
        document.getElementById("app").classList.toggle("sidebar-open");
    });
}

// --- PREENCHE O NOME DO USUÁRIO NO CABEÇALHO ---
function atualizarInfoUsuario() {
    const usuario = obterUsuario();
    document.querySelectorAll("[data-usuario-nome]").forEach(elemento => {
        elemento.textContent = usuario?.nome || "";
    });
}

// --- EXIBE APENAS OS ITENS DE MENU PERMITIDOS PARA O PERFIL ATUAL ---
function filtrarMenuPorPerfil() {
    const usuario = obterUsuario();
    const perfil = usuario?.role;
    document.querySelectorAll("[data-perfis]").forEach(elemento => {
        const permitidos = elemento.getAttribute("data-perfis").split(",").map(p => p.trim());
        elemento.hidden = !perfil || !permitidos.includes(perfil);
    });
}

// --- DESTACA O LINK ATIVO E ATUALIZA O TÍTULO DA PÁGINA ---
function marcarRotaAtiva(hash, titulo) {
    document.querySelectorAll(".sidebar-nav .nav-link").forEach(link => {
        link.classList.toggle("active", link.getAttribute("href") === hash);
    });

    const tituloElemento = document.querySelector("[data-titulo-pagina]");
    if (tituloElemento) {
        tituloElemento.textContent = titulo || "";
    }
}

// --- RENDERIZAÇÃO DA ROTA ATUAL ---
async function renderizar() {
    const hash = location.hash || "#/";

    if (hash === "#/" || hash === "") {
        location.hash = estaAutenticado() ? dashboardDoPerfil(obterUsuario().role) : "#/login";
        return;
    }

    const rota = ROTAS[hash];

    if (!rota) {
        location.hash = estaAutenticado() ? dashboardDoPerfil(obterUsuario().role) : "#/login";
        return;
    }

    if (!rota.publico && !estaAutenticado()) {
        location.hash = "#/login";
        return;
    }

    if (rota.publico && hash === "#/login" && estaAutenticado()) {
        location.hash = dashboardDoPerfil(obterUsuario().role);
        return;
    }

    if (rota.perfis && !possuiPerfil(rota.perfis)) {
        notificar("Você não tem permissão para acessar esta área.", "error");
        location.hash = dashboardDoPerfil(obterUsuario()?.role);
        return;
    }

    const app = document.getElementById("app");

    if (rota.modo === "limpo") {
        layoutMontado = false;
        app.className = "auth-screen";
        app.innerHTML = await carregarModelo(rota.modelo);
        const modulo = await rota.modulo();
        modulo.montar?.(app, {
            rota
        });
    } else {
        await garantirLayout();
        const conteudo = document.getElementById("conteudo");
        conteudo.innerHTML = await carregarModelo(rota.modelo);
        marcarRotaAtiva(hash, rota.titulo);
        document.getElementById("app").classList.remove("sidebar-open");
        const modulo = await rota.modulo();
        modulo.montar?.(conteudo, {
            rota
        });
    }
}

aplicarTemaSalvo();
window.addEventListener("hashchange", renderizar);
window.addEventListener("DOMContentLoaded", renderizar);

