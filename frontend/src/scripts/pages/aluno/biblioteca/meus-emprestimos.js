import { emprestimoApi } from "../../../remotes/biblioteca/emprestimos.js";
import { multaApi } from "../../../remotes/biblioteca/multas.js";
import { renderHeader, badge, formatDate, money } from "../../biblioteca/_shared.js";

// --- CABECALHO E REFERENCIAS DE ELEMENTOS ---
renderHeader("Portal do Aluno - Meus empréstimos", "aluno");

const semLogin          = document.getElementById("semLogin");
const secEmprestimos    = document.getElementById("secEmprestimos");
const secMultas         = document.getElementById("secMultas");
const tbodyEmp          = document.getElementById("tbodyEmp");
const tbodyMulta        = document.getElementById("tbodyMulta");
const tplEmp            = document.getElementById("rowEmp");
const tplEmpVazio       = document.getElementById("rowEmpVazio");
const tplMulta          = document.getElementById("rowMulta");
const tplMultaVazio     = document.getElementById("rowMultaVazio");

// --- ID DO USUARIO AUTENTICADO ---
const usuarioId = localStorage.getItem("usuarioId");

if (!usuarioId) {
    semLogin.hidden = false;
} else {
    secEmprestimos.hidden = false;
    secMultas.hidden = false;
    carregar();
}

// --- CARREGA EMPRESTIMOS E MULTAS DO USUARIO ---
async function carregar() {
    await carregarEmprestimos();
    await carregarMultas();
}

async function carregarEmprestimos() {
    const page = await emprestimoApi.listarPorUsuario(usuarioId, {
        size: 100,
        sort: "dataEmprestimo,desc"
    });

    const emprestimos = page.content || [];
    tbodyEmp.replaceChildren();
    if (!emprestimos.length) {
        tbodyEmp.appendChild(tplEmpVazio.content.cloneNode(true));
        return;
    }

    for (const e of emprestimos) {
        const row = tplEmp.content.cloneNode(true);
        row.querySelector("[data-cell='livro']").textContent = e.livroTitulo;
        row.querySelector("[data-cell='emprestimo']").textContent = formatDate(e.dataEmprestimo);
        row.querySelector("[data-cell='prev']").textContent = formatDate(e.dataDevolucaoPrevista);
        row.querySelector("[data-cell='devolvido']").textContent = formatDate(e.dataDevolucaoEfetiva);
        row.querySelector("[data-cell='status']").appendChild(badge(e.status));
        row.querySelector("[data-cell='renovacoes']").textContent = e.renovacoes;

        const acoes = row.querySelector("[data-cell='acoes']");
        const btnRenovar = acoes.querySelector("[data-action='renovar']");

        if (e.status !== "DEVOLVIDO") {
            btnRenovar.addEventListener("click", () => onRenovar(e.id));
        } else {
            acoes.replaceChildren();
        }
        tbodyEmp.appendChild(row);
    }
}

async function carregarMultas() {
    const multas = await multaApi.pendentesDoUsuario(usuarioId);
    tbodyMulta.replaceChildren();
    if (!multas.length) {
        tbodyMulta.appendChild(tplMultaVazio.content.cloneNode(true));
        return;
    }

    for (const m of multas) {
        const row = tplMulta.content.cloneNode(true);
        row.querySelector("[data-cell='livro']").textContent = m.livroTitulo;
        row.querySelector("[data-cell='diasAtraso']").textContent = m.diasAtraso;
        row.querySelector("[data-cell='valor']").textContent = money(m.valor);
        row.querySelector("[data-cell='gerada']").textContent = formatDate(m.geradaEm);
        tbodyMulta.appendChild(row);
    }
}

async function onRenovar(id) {
    try {
        await emprestimoApi.renovar(id);
        carregar();
    } catch (err) {
        alert(err.message);
    }
}

