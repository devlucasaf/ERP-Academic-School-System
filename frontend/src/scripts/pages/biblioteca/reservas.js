import { reservaApi } from "../../remotes/biblioteca/reservas.js";
import { renderHeader, badge, formatDate } from "./_shared.js";

// --- CABECALHO E REFERENCIAS DE ELEMENTOS ---
renderHeader("Biblioteca - Reservas");

const formFila  = document.getElementById("formFila");
const tbody     = document.getElementById("tbody");
const tplRow    = document.getElementById("rowReserva");
const tplVazio  = document.getElementById("rowVazio");

// --- CARREGA A FILA DE RESERVAS DO LIVRO ---
async function carregarFila() {
    const lista = await reservaApi.filaDoLivro(formFila.livroId.value.trim());
    tbody.replaceChildren();
    if (!lista.length) {
        tbody.appendChild(tplVazio.content.cloneNode(true));
        return;
    }

    for (const r of lista) {
        const row = tplRow.content.cloneNode(true);
        row.querySelector("[data-cell='posicao']").textContent = r.posicaoFila;
        row.querySelector("[data-cell='usuario']").textContent = r.usuarioNome;
        row.querySelector("[data-cell='data']").textContent = formatDate(r.dataReserva);
        row.querySelector("[data-cell='status']").appendChild(badge(r.status));
        row.querySelector("[data-action='cancelar']").addEventListener("click", () => onCancelar(r.id));
        tbody.appendChild(row);
    }
}

async function onCancelar(id) {
    if (!confirm("Cancelar reserva?")) {
        return;
    }

    try {
        await reservaApi.cancelar(id);
        carregarFila();
    } catch (err) {
        alert(err.message);
    }
}

formFila.addEventListener("submit", (e) => { e.preventDefault(); carregarFila(); });

