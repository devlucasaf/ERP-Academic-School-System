import { emprestimoApi } from "../../remotes/biblioteca/emprestimos.js";
import { exemplarApi } from "../../remotes/biblioteca/exemplares.js";
import { renderHeader, badge, formatDate, money, showOk, showError } from "./_shared.js";

// --- CABECALHO E REFERENCIAS DE ELEMENTOS ---
renderHeader("Biblioteca - Empréstimos");

const formEmp   = document.getElementById("formEmp");
const formCons  = document.getElementById("formCons");
const msg       = document.getElementById("msg");
const tbody     = document.getElementById("tbody");
const tplRow    = document.getElementById("rowEmp");
const tplVazio  = document.getElementById("rowVazio");

// --- REGISTRA UM NOVO EMPRESTIMO ---
formEmp.addEventListener("submit", async (e) => {
    e.preventDefault();
    try {
        const ex = await exemplarApi.buscarPorCodigo(e.target.codigoBarras.value.trim());
        const emp = await emprestimoApi.registrar({
            exemplarId: ex.id,
            usuarioId: e.target.usuarioId.value.trim()
        });
        showOk(msg, `Empréstimo #${emp.id.substring(0, 8)} registrado. ` +
            `Devolução prevista: ${formatDate(emp.dataDevolucaoPrevista)}`);
        e.target.reset();
    } catch (err) {
        showError(msg, err.message);
    }
});

// --- LISTA EMPRESTIMOS DE UM USUARIO ---
formCons.addEventListener("submit", async (e) => {
    e.preventDefault();
    const usuarioId = e.target.usuarioId.value.trim();
    const page = await emprestimoApi.listarPorUsuario(usuarioId, {
        size: 50,
        sort: "dataEmprestimo,desc"
    });
    const emprestimos = page.content || [];
    tbody.replaceChildren();
    if (!emprestimos.length) {
        tbody.appendChild(tplVazio.content.cloneNode(true));
        return;
    }
    for (const emp of emprestimos) {
        const row = tplRow.content.cloneNode(true);
        row.querySelector("[data-cell='livro']").textContent = emp.livroTitulo;
        row.querySelector("[data-cell='codigo']").textContent = emp.exemplarCodigoBarras;
        row.querySelector("[data-cell='emprestimo']").textContent = formatDate(emp.dataEmprestimo);
        row.querySelector("[data-cell='prev']").textContent = formatDate(emp.dataDevolucaoPrevista);
        row.querySelector("[data-cell='devolvido']").textContent = formatDate(emp.dataDevolucaoEfetiva);
        row.querySelector("[data-cell='status']").appendChild(badge(emp.status));
        row.querySelector("[data-cell='renovacoes']").textContent = emp.renovacoes;
        row.querySelector("[data-cell='multa']").textContent = money(emp.valorMulta);
        tbody.appendChild(row);
    }
});

