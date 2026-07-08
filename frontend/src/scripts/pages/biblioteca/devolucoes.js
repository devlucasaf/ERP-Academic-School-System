import { emprestimoApi } from "../../remotes/biblioteca/emprestimos.js";
import { renderHeader, badge, formatDate, money, showOk, showError } from "./_shared.js";

// --- CABECALHO E REFERENCIAS DE ELEMENTOS ---
renderHeader("Biblioteca - Devoluções");

const form = document.getElementById("form");
const msg = document.getElementById("msg");
const detalhe = document.getElementById("detalhe");

// --- EXECUTA DEVOLUCAO OU RENOVACAO E EXIBE O DETALHE ---
async function processar(acao) {
    const empId = form.empId.value.trim();
    try {
        const r = acao === "devolver"
            ? await emprestimoApi.devolver(empId)
            : await emprestimoApi.renovar(empId);
        showOk(msg, "Operação concluída.");
        preencherDetalhe(r);
    } catch (err) {
        showError(msg, err.message);
    }
}

// --- PREENCHE A TABELA DE DETALHE ---
function preencherDetalhe(r) {
    detalhe.querySelector("[data-cell='livro']").textContent = r.livroTitulo;
    detalhe.querySelector("[data-cell='exemplar']").textContent = r.exemplarCodigoBarras;
    detalhe.querySelector("[data-cell='usuario']").textContent = r.usuarioNome;
    const statusCell = detalhe.querySelector("[data-cell='status']");
    statusCell.replaceChildren(badge(r.status));
    detalhe.querySelector("[data-cell='prev']").textContent = formatDate(r.dataDevolucaoPrevista);
    detalhe.querySelector("[data-cell='devolvido']").textContent = formatDate(r.dataDevolucaoEfetiva);
    detalhe.querySelector("[data-cell='diasAtraso']").textContent = r.diasAtraso;
    detalhe.querySelector("[data-cell='multa']").textContent = money(r.valorMulta);
    detalhe.querySelector("[data-cell='renovacoes']").textContent = r.renovacoes;
    detalhe.hidden = false;
}

form.addEventListener("submit", (e) => { e.preventDefault(); processar("devolver"); });
document.getElementById("btnRen").addEventListener("click", () => processar("renovar"));

