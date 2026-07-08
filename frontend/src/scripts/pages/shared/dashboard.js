import { obterUsuario } from "../../auth.js";

export function criarDashboard(titulo) {
    return function montar(raiz) {
        const usuario = obterUsuario();
        const tituloElemento = raiz.querySelector("[data-campo='titulo']");
        const boasVindasElemento = raiz.querySelector("[data-campo='boasVindas']");
        if (tituloElemento) {
            tituloElemento.textContent = titulo;
        }

        if (boasVindasElemento) {
            boasVindasElemento.textContent = `Bem-vindo, ${usuario?.nome || "usuário"}!`;
        }
    };
}

