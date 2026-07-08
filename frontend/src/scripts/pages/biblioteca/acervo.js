import { livroApi } from "../../remotes/biblioteca/livros.js";
import { renderHeader, formatDate, showOk, showError } from "./_shared.js";

// --- CABECALHO E REFERENCIAS DE ELEMENTOS ---
renderHeader("Biblioteca - Acervo");

const formLivro = document.getElementById("formLivro");
const formBusca = document.getElementById("formBusca");
const tbody     = document.getElementById("tbodyLivros");
const listaMsg  = document.getElementById("listaMsg");
const formMsg   = document.getElementById("formMsg");
const tplRow    = document.getElementById("rowLivro");
const tplVazio  = document.getElementById("rowVazio");

let livroEmEdicao = null;
let livrosCarregados = [];

// --- CARREGA E RENDERIZA A LISTA ---
async function listar(params = {}) {
    try {
        listaMsg.hidden = true;
        const page = await livroApi.buscar({ size: 20, ...params });
        livrosCarregados = page.content || page || [];
        renderLista(livrosCarregados);
    } catch (e) {
        livrosCarregados = [];
        renderLista([]);
        showError(listaMsg, e.message);
    }
}

// --- MONTA AS LINHAS CLONANDO O TEMPLATE ---
function renderLista(livros) {
    tbody.replaceChildren();
    if (!livros.length) {
        tbody.appendChild(tplVazio.content.cloneNode(true));
        return;
    }

    for (const l of livros) {
        const row = tplRow.content.cloneNode(true);
        row.querySelector("[data-cell='titulo']").textContent = l.titulo;
        row.querySelector("[data-cell='autor']").textContent = l.autor;
        row.querySelector("[data-cell='categoria']").textContent = l.categoria || "-";
        row.querySelector("[data-cell='isbn']").textContent = l.isbn || "-";
        row.querySelector("[data-cell='exemplares']").textContent =
            `${l.exemplaresDisponiveis}/${l.totalExemplares}`;
        row.querySelector("[data-cell='criado']").textContent = formatDate(l.criadoEm);
        row.querySelector("[data-action='editar']").addEventListener("click", () => onEditar(l.id));
        row.querySelector("[data-action='excluir']").addEventListener("click", () => onExcluir(l.id));
        tbody.appendChild(row);
    }
}

// --- PREENCHE O FORMULARIO PARA EDICAO ---
function preencherForm(l) {
    ["titulo", "autor", "isbn", "editora", "anoPublicacao", "edicao", "paginas", "categoria", "sinopse"]
        .forEach(k => { if (formLivro[k]) {
            formLivro[k].value = l[k] ?? "";
        }
    });
}

function onEditar(id) {
    livroEmEdicao = livrosCarregados.find(l => l.id === id);
    if (livroEmEdicao) {
        preencherForm(livroEmEdicao);
        window.scrollTo(0, 0);
    }
}

async function onExcluir(id) {
    if (!confirm("Confirmar exclusão do livro?")) {
        return;
    }

    try {
        await livroApi.deletar(id);
        livroEmEdicao = null;
        listar();
    } catch (e) {
        alert(e.message);
    }
}

// --- SALVAR ---
formLivro.addEventListener("submit", async (e) => {
    e.preventDefault();
    const f = e.target;
    const dados = {
        titulo: f.titulo.value,
        autor: f.autor.value,
        isbn: f.isbn.value || null,
        editora: f.editora.value || null,
        anoPublicacao: f.anoPublicacao.value ? Number(f.anoPublicacao.value) : null,
        edicao: f.edicao.value || null,
        paginas: f.paginas.value ? Number(f.paginas.value) : null,
        categoria: f.categoria.value || null,
        sinopse: f.sinopse.value || null
    };

    const capa = f.capa.files[0];
    try {
        if (livroEmEdicao) {
            await livroApi.atualizar(livroEmEdicao.id, dados, capa);
            showOk(formMsg, "Atualizado.");
        } else {
            await livroApi.criar(dados, capa);
            showOk(formMsg, "Cadastrado.");
        }
        livroEmEdicao = null;
        f.reset();
        listar();
    } catch (err) {
        showError(formMsg, err.message);
    }
});

// --- LIMPAR FORMULARIO DE CADASTRO ---
document.getElementById("btnLimpar").addEventListener("click", () => {
    livroEmEdicao = null;
    formLivro.reset();
});

// --- APLICAR FILTROS DE BUSCA ---
formBusca.addEventListener("submit", (e) => {
    e.preventDefault();
    const f = new FormData(e.target);
    const params = {};
    for (const [k, v] of f.entries()) {
        if (v) {
            params[k] = v;
        }
    }
    listar(params);
});

document.getElementById("btnLimparBusca").addEventListener("click", () => {
    formBusca.reset();
    listar();
});

listar();

