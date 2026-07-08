import { livroApi } from "../../remotes/biblioteca/livros.js";
import { exemplarApi } from "../../remotes/biblioteca/exemplares.js";
import { renderHeader, badge, formatDate, showOk, showError } from "./_shared.js";

// --- CABECALHO E REFERENCIAS DE ELEMENTOS ---
renderHeader("Biblioteca - Exemplares");

const formBuscaLivro    = document.getElementById("formBuscaLivro");
const resultadosLivro   = document.getElementById("resultadosLivro");
const buscaMsg          = document.getElementById("buscaMsg");
const cardExemplares    = document.getElementById("cardExemplares");
const tituloLivro       = document.getElementById("tituloLivro");
const formExemplar      = document.getElementById("formExemplar");
const msgExemplar       = document.getElementById("msgExemplar");
const tbodyEx           = document.getElementById("tbodyEx");
const tplItemLivro      = document.getElementById("itemLivro");
const tplRowEx          = document.getElementById("rowExemplar");
const tplVazioLivro     = document.getElementById("rowVazioLivro");
const tplVazioEx        = document.getElementById("rowVazioEx");

let livroSelecionado = null;

// --- BUSCA LIVROS PELO TITULO ---
formBuscaLivro.addEventListener("submit", async (e) => {
    e.preventDefault();
    buscaMsg.hidden = true;
    resultadosLivro.replaceChildren();
    try {
        const page = await livroApi.buscar({
            titulo: e.target.titulo.value,
            size: 10
        });
        const livros = page.content || [];
        if (!livros.length) {
            resultadosLivro.appendChild(tplVazioLivro.content.cloneNode(true));
            return;
        }

        for (const l of livros) {
            const item = tplItemLivro.content.cloneNode(true);
            const link = item.querySelector("[data-action='selecionar']");
            link.textContent = `${l.titulo} — ${l.autor}`;
            link.addEventListener("click", (ev) => {
                ev.preventDefault();
                selecionarLivro(l);
            });
            resultadosLivro.appendChild(item);
        }
    } catch (err) {
        showError(buscaMsg, err.message);
    }
});

// --- SELECIONA O LIVRO E EXIBE SEUS EXEMPLARES ---
function selecionarLivro(l) {
    livroSelecionado = {
        id: l.id,
        titulo: l.titulo
    };
    tituloLivro.textContent = l.titulo;
    cardExemplares.hidden = false;
    listarExemplares();
}

// --- GERA CODIGO DE BARRAS AUTOMATICAMENTE ---
document.getElementById("btnGerar").addEventListener("click", async () => {
    const r = await exemplarApi.gerarCodigoBarras();
    formExemplar.codigoBarras.value = r.codigoBarras;
});

// --- CRIA UM NOVO EXEMPLAR ---
formExemplar.addEventListener("submit", async (e) => {
    e.preventDefault();
    try {
        await exemplarApi.criar({
            livroId: livroSelecionado.id,
            codigoBarras: e.target.codigoBarras.value || null,
            localizacao: e.target.localizacao.value || null
        });
        e.target.reset();
        showOk(msgExemplar, "Exemplar criado.");
        listarExemplares();
    } catch (err) {
        showError(msgExemplar, err.message);
    }
});

// --- LISTA OS EXEMPLARES DO LIVRO SELECIONADO ---
async function listarExemplares() {
    const page = await exemplarApi.listarPorLivro(livroSelecionado.id, { size: 50 });
    const exemplares = page.content || [];
    tbodyEx.replaceChildren();
    if (!exemplares.length) {
        tbodyEx.appendChild(tplVazioEx.content.cloneNode(true));
        return;
    }

    for (const ex of exemplares) {
        const row = tplRowEx.content.cloneNode(true);
        row.querySelector("[data-cell='codigo']").textContent = ex.codigoBarras;
        row.querySelector("[data-cell='localizacao']").textContent = ex.localizacao || "-";
        row.querySelector("[data-cell='status']").appendChild(badge(ex.status));
        row.querySelector("[data-cell='criado']").textContent = formatDate(ex.criadoEm);
        row.querySelector("[data-action='excluir']").addEventListener("click", () => onExcluir(ex.id));
        tbodyEx.appendChild(row);
    }
}

async function onExcluir(id) {
    if (!confirm("Excluir exemplar?")) {
        return;
    }

    try {
        await exemplarApi.deletar(id);
        listarExemplares();
    } catch (err) {
        alert(err.message);
    }
}

