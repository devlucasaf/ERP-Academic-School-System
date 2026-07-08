import { reservaApi } from "../../../remotes/biblioteca/reservas.js";
import { renderHeader, badge, formatDate } from "../../biblioteca/_shared.js";

// --- CABECALHO E REFERENCIAS DE ELEMENTOS ---
renderHeader("Portal do Aluno - Minhas reservas", "aluno");

const semLogin      = document.getElementById("semLogin");
const secReservas   = document.getElementById("secReservas");
const tbody         = document.getElementById("tbody");
const tplRow        = document.getElementById("rowReserva");
const tplVazio      = document.getElementById("rowVazio");

// --- ID DO USUARIO AUTENTICADO ---
const usuarioId = localStorage.getItem("usuarioId");

if (!usuarioId) {
    semLogin.hidden = false;
} else {
    secReservas.hidden = false;
    carregar();
}

// --- CARREGA AS RESERVAS DO USUARIO ---
async function carregar() {
    const lista = await reservaApi.doUsuario(usuarioId);
    tbody.replaceChildren();
    if (!lista.length) {
        tbody.appendChild(tplVazio.content.cloneNode(true));
        return;
    }

    for (const r of lista) {
        const row = tplRow.content.cloneNode(true);
        row.querySelector("[data-cell='livro']").textContent = r.livroTitulo;
        row.querySelector("[data-cell='data']").textContent = formatDate(r.dataReserva);
        row.querySelector("[data-cell='status']").appendChild(badge(r.status));
        row.querySelector("[data-cell='posicao']").textContent = r.posicaoFila;

        const acoes = row.querySelector("[data-cell='acoes']");
        const btnCancelar = acoes.querySelector("[data-action='cancelar']");

        if (r.status === "AGUARDANDO") {
            btnCancelar.addEventListener("click", () => onCancelar(r.id));
        } else {
            acoes.replaceChildren();
        }
        tbody.appendChild(row);
    }
}

async function onCancelar(id) {
    if (!confirm("Cancelar reserva?")) {
        return;
    }

    try {
        await reservaApi.cancelar(id);
        carregar();
    } catch (err) {
        alert(err.message);
    }
}

