import { notificar } from "../../util.js";

// --- MONTA A LANDING PAGE DO SITE INSTITUCIONAL ---
export function montar(raiz) {
    const cabecalho = raiz.querySelector("#siteHeader");
    const menu = raiz.querySelector("#siteMenu");
    const burger = raiz.querySelector("#siteBurger");

    // --- ABRE/FECHA O MENU NO MOBILE ---
    burger?.addEventListener("click", () => {
        const aberto = menu.classList.toggle("aberto");
        burger.classList.toggle("ativo", aberto);
        burger.setAttribute("aria-expanded", String(aberto));
    });

    // --- ROLAGEM SUAVE ATE AS SECOES ---
    raiz.querySelectorAll("[data-scroll]").forEach((elemento) => {
        elemento.addEventListener("click", (evento) => {
            evento.preventDefault();
            const alvo = raiz.querySelector(`#${elemento.getAttribute("data-scroll")}`);
            if (alvo) {
                alvo.scrollIntoView({ behavior: "smooth", block: "start" });
            }
            menu?.classList.remove("aberto");
            burger?.classList.remove("ativo");
            burger?.setAttribute("aria-expanded", "false");
        });
    });

    // --- BOTOES QUE LEVAM AO LOGIN DO SISTEMA ---
    raiz.querySelectorAll("#btnPortal, #btnPortalHero, #btnPortalAlunos").forEach((botao) => {
        botao.addEventListener("click", () => {
            location.hash = "#/login";
        });
    });

    // --- SOMBRA NO CABECALHO AO ROLAR A PAGINA ---
    const aoRolar = () => {
        cabecalho?.classList.toggle("com-sombra", window.scrollY > 10);
    };
    aoRolar();
    window.addEventListener("scroll", aoRolar, { passive: true });

    // --- FORMULARIO DE CONTATO ---
    const formulario = raiz.querySelector("#siteForm");
    const mensagem = raiz.querySelector("#siteFormMsg");
    formulario?.addEventListener("submit", (evento) => {
        evento.preventDefault();
        formulario.reset();
        if (mensagem) {
            mensagem.textContent = "Mensagem enviada! Em breve entraremos em contato.";
            mensagem.hidden = false;
        }
        notificar("Mensagem enviada com sucesso!", "success");
    });
}

