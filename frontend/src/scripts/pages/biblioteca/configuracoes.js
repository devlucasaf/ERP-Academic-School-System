import { configuracaoBibliotecaApi } from "../../remotes/biblioteca/configuracoes.js";
import { renderHeader, showOk, showError } from "./_shared.js";

// --- CABECALHO E REFERENCIAS DE ELEMENTOS ---
renderHeader("Biblioteca - Configurações");

const form = document.getElementById("form");
const msg = document.getElementById("msg");
const CAMPOS = [
    "prazoEmprestimoAluno",
    "prazoEmprestimoProfessor",
    "maxEmprestimosSimultaneos",
    "maxRenovacoes",
    "valorMultaDia"
];

// --- CARREGA OS VALORES ATUAIS NO FORMULARIO ---
async function carregar() {
    try {
        const c = await configuracaoBibliotecaApi.obter();
        CAMPOS.forEach(k => { form[k].value = c[k]; });
    } catch (err) {
        showError(msg, err.message);
    }
}

// --- SALVA OS PARAMETROS ---
form.addEventListener("submit", async (e) => {
    e.preventDefault();
    try {
        const dados = {};
        CAMPOS.forEach(k => { dados[k] = Number(form[k].value); });
        await configuracaoBibliotecaApi.atualizar(dados);
        showOk(msg, "Salvo.");
    } catch (err) {
        showError(msg, err.message);
    }
});

carregar();

