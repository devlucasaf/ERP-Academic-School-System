import { livroApi } from "../../../remotes/biblioteca/livros.js";
import { reservaApi } from "../../../remotes/biblioteca/reservas.js";
import { renderHeader } from "../../biblioteca/_shared.js";

// --- CABECALHO E REFERENCIAS DE ELEMENTOS ---
renderHeader("Portal do Aluno - Biblioteca", "aluno");

const form          = document.getElementById("form");
const tbody         = document.getElementById("tbody");
const infoPagina    = document.getElementById("infoPagina");
const btnPrev       = document.getElementById("prev");
const btnNext       = document.getElementById("next");
const tplDisponivel = document.getElementById("rowDisponivel");
const tplReservar   = document.getElementById("rowReservar");
const tplVazio      = document.getElementById("rowVazio");

let paginaAtual = 0;
let filtroAtual = {};

// --- BUSCA O ACERVO COM FILTROS E PAGINACAO ---
async function buscar() {
    const page = await livroApi.buscar({
        ...filtroAtual,
        page: paginaAtual,
        size: 10
    });
    const livros = page.content || [];
    renderLinhas(livros);

    infoPagina.textContent = `Página ${page.number + 1} de ${page.totalPages || 1}`;
    btnPrev.disabled = page.first;
    btnNext.disabled = page.last;
}

// --- MONTA AS LINHAS ESCOLHENDO O TEMPLATE CONFORME DISPONIBILIDADE ---
function renderLinhas(livros) {
    tbody.replaceChildren();
    if (!livros.length) {
        tbody.appendChild(tplVazio.content.cloneNode(true));
        return;
    }

    for (const l of livros) {
        const disponivel = l.exemplaresDisponiveis > 0;
        const row = (disponivel ? tplDisponivel : tplReservar).content.cloneNode(true);
        row.querySelector("[data-cell='titulo']").textContent = l.titulo;
        row.querySelector("[data-cell='autor']").textContent = l.autor;
        row.querySelector("[data-cell='categoria']").textContent = l.categoria || "-";
        row.querySelector("[data-cell='disponiveis']").textContent =
            `${l.exemplaresDisponiveis}/${l.totalExemplares}`;

        if (!disponivel) {
            row.querySelector("[data-action='reservar']").addEventListener("click", () => onReservar(l.id));
        }
        tbody.appendChild(row);
    }
}

async function onReservar(livroId) {
    try {
        const r = await reservaApi.reservar({ livroId });
        alert(`Reservado! Posição na fila: ${r.posicaoFila}`);
    } catch (err) {
        alert(err.message);
    }
}

form.addEventListener("submit", (e) => {
    e.preventDefault();
    filtroAtual = {};
    const f = new FormData(e.target);
    for (const [k, v] of f.entries()) {
        if (v) {
            filtroAtual[k] = v;
        }
    }
    paginaAtual = 0;
    buscar();
});

btnPrev.addEventListener("click", () => { paginaAtual--; buscar(); });
btnNext.addEventListener("click", () => { paginaAtual++; buscar(); });

buscar();

