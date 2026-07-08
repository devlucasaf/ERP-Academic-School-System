import { autenticar, dashboardDoPerfil } from "../../auth.js";
import { notificar } from "../../util.js";

// --- LIGA O SUBMIT DO FORMULÁRIO ---
export function montar(raiz) {
    const formulario = raiz.querySelector("#formularioLogin");
    const mensagem = raiz.querySelector("#mensagemLogin");
    const botao = raiz.querySelector("#btnEntrar");

    formulario.addEventListener("submit", async (evento) => {
        evento.preventDefault();
        mensagem.hidden = true;

        const email = formulario.email.value.trim();
        const senha = formulario.senha.value;

        // --- VALIDAÇÃO SIMPLES ---
        if (!email || !senha) {
            mensagem.textContent = "Informe e-mail e senha.";
            mensagem.hidden = false;
            return;
        }

        botao.disabled = true;
        botao.textContent = "Entrando...";
        try {
            const usuario = await autenticar(email, senha);
            notificar(`Bem-vindo, ${usuario.nome}!`, "success");
            location.hash = dashboardDoPerfil(usuario.role);
        } catch (erro) {
            mensagem.textContent = erro.message;
            mensagem.hidden = false;
            notificar(erro.message, "error");
        } finally {
            botao.disabled = false;
            botao.textContent = "Entrar";
        }
    });
}

