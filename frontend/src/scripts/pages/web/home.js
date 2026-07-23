import { notificar } from "../../util.js";

const CHAVE_TEMA = "theme";

// --- MONTA A LANDING PAGE DO SITE INSTITUCIONAL ---
export function montar(raiz) {
    const cabecalho = raiz.querySelector("#siteHeader");
    const menu = raiz.querySelector("#siteMenu");
    const burger = raiz.querySelector("#siteBurger");

    aplicarTemaNoBotao(raiz);
    configurarBotaoTema(raiz);
    configurarMenuMobile(menu, burger);
    configurarRolagemSuave(raiz, menu, burger);
    configurarBotoesPortal(raiz);
    configurarSombraCabecalho(cabecalho);
    configurarSliderNoticias(raiz);
    configurarFormularioContato(raiz);
}

// --- ABRE E FECHA O MENU HAMBURGER NO MOBILE ---
function configurarMenuMobile(menu, burger) {
    burger?.addEventListener("click", () => {
        const aberto = menu.classList.toggle("aberto");
        burger.classList.toggle("ativo", aberto);
        burger.setAttribute("aria-expanded", String(aberto));
    });
}

// --- ROLAGEM SUAVE PARA AS SECOES DA PAGINA ---
function configurarRolagemSuave(raiz, menu, burger) {
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
}

// --- BOTOES QUE LEVAM AO LOGIN DO SISTEMA ---
function configurarBotoesPortal(raiz) {
    raiz.querySelectorAll("#btnPortal, #btnPortalHero, #btnPortalAlunos").forEach((botao) => {
        botao.addEventListener("click", () => {
            location.hash = "#/login";
        });
    });
}

// --- SOMBRA NO CABECALHO QUANDO A PAGINA E ROLADA ---
function configurarSombraCabecalho(cabecalho) {
    const aoRolar = () => {
        cabecalho?.classList.toggle("com-sombra", window.scrollY > 10);
    };
    aoRolar();
    window.addEventListener("scroll", aoRolar, { passive: true });
}

// --- SLIDER DE NOTICIAS EM DESTAQUE ---
function configurarSliderNoticias(raiz) {
    const slides = Array.from(raiz.querySelectorAll(".site-noticia-slide"));
    const pontos = Array.from(raiz.querySelectorAll(".site-slider-ponto"));
    const btnAnterior = raiz.querySelector("[data-slider-anterior]");
    const btnProximo = raiz.querySelector("[data-slider-proximo]");

    if (slides.length === 0) {
        return;
    }

    let indiceAtual = 0;
    let temporizador = null;

    // --- EXIBE APENAS O SLIDE INDICADO E ATUALIZA OS PONTOS ---
    const irPara = (indice) => {
        indiceAtual = (indice + slides.length) % slides.length;
        slides.forEach((slide, i) => slide.classList.toggle("ativo", i === indiceAtual));
        pontos.forEach((ponto, i) => ponto.classList.toggle("ativo", i === indiceAtual));
    };

    // --- AVANCA PARA O PROXIMO SLIDE AUTOMATICAMENTE ---
    const iniciarRotacao = () => {
        pararRotacao();
        temporizador = setInterval(() => irPara(indiceAtual + 1), 6000);
    };

    const pararRotacao = () => {
        if (temporizador) {
            clearInterval(temporizador);
            temporizador = null;
        }
    };

    // --- CONTROLES DO USUARIO ---
    btnAnterior?.addEventListener("click", () => {
        irPara(indiceAtual - 1);
        iniciarRotacao();
    });

    btnProximo?.addEventListener("click", () => {
        irPara(indiceAtual + 1);
        iniciarRotacao();
    });

    pontos.forEach((ponto) => {
        ponto.addEventListener("click", () => {
            irPara(Number(ponto.getAttribute("data-ponto")));
            iniciarRotacao();
        });
    });

    // --- PAUSA A ROTACAO QUANDO O MOUSE ESTA SOBRE O SLIDER ---
    const container = raiz.querySelector(".site-noticias-slider");
    container?.addEventListener("mouseenter", pararRotacao);
    container?.addEventListener("mouseleave", iniciarRotacao);

    iniciarRotacao();
}

// --- APLICA O ESTADO VISUAL DO BOTAO DE TEMA CONFORME O TEMA ATUAL ---
function aplicarTemaNoBotao(raiz) {
    const botao = raiz.querySelector("#btnTemaSite");
    if (!botao) return;
    const escuro = document.documentElement.getAttribute("data-theme") === "dark";
    botao.classList.toggle("noturno", escuro);
}

// --- ALTERNA O TEMA COM ANIMACAO DIVERTIDA ---
function configurarBotaoTema(raiz) {
    const botao = raiz.querySelector("#btnTemaSite");
    if (!botao) return;

    botao.addEventListener("click", () => {
        criarOndaDeTema(botao);

        botao.classList.add("girando");
        setTimeout(() => botao.classList.remove("girando"), 600);

        // --- ALTERNA O TEMA APOS UM PEQUENO DELAY PARA SINCRONIZAR COM A ONDA ---
        setTimeout(() => {
            const atual = document.documentElement.getAttribute("data-theme") === "dark" ? "dark" : "light";
            const novo = atual === "dark" ? "light" : "dark";
            document.documentElement.setAttribute("data-theme", novo);
            localStorage.setItem(CHAVE_TEMA, novo);
            botao.classList.toggle("noturno", novo === "dark");
        }, 150);
    });
}

// --- CRIA UMA ONDA CIRCULAR EXPANSIVA A PARTIR DO BOTAO CLICADO ---
function criarOndaDeTema(botao) {
    const retangulo = botao.getBoundingClientRect();
    const onda = document.createElement("span");
    onda.className = "site-tema-onda";

    const centroX = retangulo.left + retangulo.width / 2;
    const centroY = retangulo.top + retangulo.height / 2;
    onda.style.left = `${centroX}px`;
    onda.style.top = `${centroY}px`;

    const proximoTema = document.documentElement.getAttribute("data-theme") === "dark" ? "light" : "dark";
    onda.dataset.tema = proximoTema;

    document.body.appendChild(onda);

    onda.addEventListener("animationend", () => onda.remove(), { once: true });
}

// --- FORMULARIO DE CONTATO ---
function configurarFormularioContato(raiz) {
    const formulario = raiz.querySelector("#siteForm");
    const mensagem = raiz.querySelector("#siteFormMsg");

    formulario?.addEventListener("submit", (evento) => {
        evento.preventDefault();

        if (!formulario.checkValidity()) {
            formulario.reportValidity();
            return;
        }

        formulario.reset();
        if (mensagem) {
            mensagem.textContent = "Mensagem enviada! Em breve entraremos em contato.";
            mensagem.hidden = false;
        }
        notificar("Mensagem enviada com sucesso!", "success");
    });
}

