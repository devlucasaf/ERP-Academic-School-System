import { multaApi } from "../../remotes/biblioteca/multas.js";
import { renderHeader, badge, formatDate, money } from "./_shared.js";

// --- CABECALHO E REFERENCIAS DE ELEMENTOS ---
renderHeader("Biblioteca - Multas");

const formFiltro    = document.getElementById("formFiltro");
const tbody         = document.getElementById("tbody");
const tplRow        = document.getElementById("rowMulta");
const tplVazio      = document.getElementById("rowVazio");

// --- LISTA AS MULTAS PELO STATUS SELECIONADO ---
async function listar(status = "PENDENTE") {
    const page = await multaApi.listar({ status, size: 50 });
    const multas = page.content || [];
    tbody.replaceChildren();
    if (!multas.length) {
        tbody.appendChild(tplVazio.content.cloneNode(true));
        return;
    }
    for (const m of multas) {
        const row = tplRow.content.cloneNode(true);
        row.querySelector("[data-cell='usuario']").textContent = m.usuarioNome;
        row.querySelector("[data-cell='livro']").textContent = m.livroTitulo;
        row.querySelector("[data-cell='diasAtraso']").textContent = m.diasAtraso;
        row.querySelector("[data-cell='valor']").textContent = money(m.valor);
        row.querySelector("[data-cell='gerada']").textContent = formatDate(m.geradaEm);
        row.querySelector("[data-cell='paga']").textContent = formatDate(m.pagaEm);
        row.querySelector("[data-cell='status']").appendChild(badge(m.status));

        const acoes = row.querySelector("[data-cell='acoes']");
        const btnPagar = acoes.querySelector("[data-action='pagar']");
        const btnCancelar = acoes.querySelector("[data-action='cancelar']");

        if (m.status === "PENDENTE") {
            btnPagar.addEventListener("click", () => onPagar(m.id, status));
            btnCancelar.addEventListener("click", () => onCancelar(m.id, status));
        } else {
            acoes.replaceChildren();
        }
        tbody.appendChild(row);
    }
}

async function onPagar(id, status) {
    try {
        await multaApi.pagar(id);
        listar(status);
    } catch (err) {
        alert(err.message);
    }
}

async function onCancelar(id, status) {
    if (!confirm("Cancelar multa?")) {
        return;
    }

    try {
        await multaApi.cancelar(id);
        listar(status);
    } catch (err) {
        alert(err.message);
    }
}

formFiltro.addEventListener("submit", (e) => {
    e.preventDefault();
    listar(e.target.status.value);
});

listar();

