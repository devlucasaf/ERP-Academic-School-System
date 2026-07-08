import { funcionariosApi } from "../../remotes/funcionarios/index.js";
import {
    notificar, formatarData, coletarFormulario,
    renderPaginacao, abrirModal, fecharModal, filtrarLinhas
} from "./_comum.js";

export function montar(raiz) {
    const tbody             = raiz.querySelector("#tbody");
    const paginacao         = raiz.querySelector("#paginacao");
    const tplLinha          = raiz.querySelector("#linha");
    const tplVazia          = raiz.querySelector("#linhaVazia");
    const formFiltro        = raiz.querySelector("#formFiltro");
    const modal             = raiz.querySelector("#modal");
    const modalTitulo       = raiz.querySelector("#modalTitulo");
    const modalMsg          = raiz.querySelector("#modalMsg");
    const formFuncionario   = raiz.querySelector("#formFuncionario");

    let paginaAtual = 0;
    let cargoAtual = "";
    let buscaAtual = "";
    let editandoId = null;

    async function carregar(pagina = 0) {
        paginaAtual = pagina;
        try {
            const page = await funcionariosApi.listar({
                page: pagina,
                size: 10,
                sort: "dataAdmissao,desc",
                cargo: cargoAtual
            });
            renderTabela(page.content || []);
            renderPaginacao(paginacao, page, carregar);
            filtrarLinhas(tbody, buscaAtual);
        } catch (erro) {
            notificar(erro.message, "error");
        }
    }

    function renderTabela(itens) {
        tbody.replaceChildren();
        if (!itens.length) {
            tbody.appendChild(tplVazia.content.cloneNode(true));
            return;
        }
        for (const item of itens) {
            const linha = tplLinha.content.cloneNode(true);
            linha.querySelector("[data-campo='nome']").textContent = item.usuario?.nome || "-";
            linha.querySelector("[data-campo='email']").textContent = item.usuario?.email || "-";
            linha.querySelector("[data-campo='cargo']").textContent = item.cargo;
            linha.querySelector("[data-campo='departamento']").textContent = item.departamento || "-";
            linha.querySelector("[data-campo='admissao']").textContent = formatarData(item.dataAdmissao);
            linha.querySelector("[data-acao='editar']").addEventListener("click", () => abrirEdicao(item));
            linha.querySelector("[data-acao='excluir']").addEventListener("click", () => excluir(item));
            tbody.appendChild(linha);
        }
    }

    function abrirNovo() {
        editandoId = null;
        formFuncionario.reset();
        modalMsg.hidden = true;
        modalTitulo.textContent = "Novo funcionário";
        abrirModal(modal);
    }

    function abrirEdicao(item) {
        editandoId = item.id;
        modalMsg.hidden = true;
        modalTitulo.textContent = "Editar funcionário";
        formFuncionario.nome.value = item.usuario?.nome || "";
        formFuncionario.email.value = item.usuario?.email || "";
        formFuncionario.cpf.value = item.usuario?.cpf || "";
        formFuncionario.telefone.value = item.usuario?.telefone || "";
        formFuncionario.dataNascimento.value = item.usuario?.dataNascimento || "";
        formFuncionario.cargo.value = item.cargo || "SECRETARIA";
        formFuncionario.dataAdmissao.value = item.dataAdmissao || "";
        formFuncionario.departamento.value = item.departamento || "";
        abrirModal(modal);
    }

    async function excluir(item) {
        if (!confirm(`Excluir o funcionário "${item.usuario?.nome}"?`)) {
            return;
        }

        try {
            await funcionariosApi.excluir(item.id);
            notificar("Funcionário excluído.", "success");
            carregar(paginaAtual);
        } catch (erro) {
            notificar(erro.message, "error");
        }
    }

    formFuncionario.addEventListener("submit", async (evento) => {
        evento.preventDefault();
        modalMsg.hidden = true;
        const dados = coletarFormulario(formFuncionario);

        try {
            if (editandoId) {
                await funcionariosApi.atualizar(editandoId, dados);
                notificar("Funcionário atualizado.", "success");
            } else {
                await funcionariosApi.criar(dados);
                notificar("Funcionário criado. Senha temporária enviada por e-mail (ver log do servidor).", "success");
            }
            fecharModal(modal);
            carregar(editandoId ? paginaAtual : 0);
        } catch (erro) {
            modalMsg.textContent = erro.message;
            modalMsg.hidden = false;
        }
    });

    formFiltro.addEventListener("submit", (evento) => {
        evento.preventDefault();
        cargoAtual = formFiltro.cargo.value;
        buscaAtual = formFiltro.busca.value;
        carregar(0);
    });

    raiz.querySelector("#btnNovo").addEventListener("click", abrirNovo);
    raiz.querySelector("#btnFechar").addEventListener("click", () => fecharModal(modal));
    raiz.querySelector("#btnCancelar").addEventListener("click", () => fecharModal(modal));

    carregar(0);
}

