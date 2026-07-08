// --- TELA DE RESPONSÁVEIS (LISTAGEM, MODAL DE CRIAR/EDITAR, EXCLUSÃO) ---
import { responsaveisApi } from '../../remotes/responsaveis/index.js';
import {
    notificar, coletarFormulario,
    renderPaginacao, abrirModal, fecharModal, filtrarLinhas
} from './_comum.js';

export function montar(raiz) {
    const tbody = raiz.querySelector('#tbody');
    const paginacao = raiz.querySelector('#paginacao');
    const tplLinha = raiz.querySelector('#linha');
    const tplVazia = raiz.querySelector('#linhaVazia');
    const formFiltro = raiz.querySelector('#formFiltro');
    const modal = raiz.querySelector('#modal');
    const modalTitulo = raiz.querySelector('#modalTitulo');
    const modalMsg = raiz.querySelector('#modalMsg');
    const formResponsavel = raiz.querySelector('#formResponsavel');

    let paginaAtual = 0;
    let buscaAtual = '';
    let editandoId = null;

    async function carregar(pagina = 0) {
        paginaAtual = pagina;
        try {
            const page = await responsaveisApi.listar({ page: pagina, size: 10, sort: 'criadoEm,desc' });
            renderTabela(page.content || []);
            renderPaginacao(paginacao, page, carregar);
            filtrarLinhas(tbody, buscaAtual);
        } catch (erro) {
            notificar(erro.message, 'error');
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
            linha.querySelector('[data-campo="nome"]').textContent = item.usuario?.nome || '-';
            linha.querySelector('[data-campo="email"]').textContent = item.usuario?.email || '-';
            linha.querySelector('[data-campo="telefone"]').textContent = item.usuario?.telefone || '-';
            linha.querySelector('[data-campo="parentesco"]').textContent = item.parentesco;
            linha.querySelector('[data-acao="editar"]').addEventListener('click', () => abrirEdicao(item));
            linha.querySelector('[data-acao="excluir"]').addEventListener('click', () => excluir(item));
            tbody.appendChild(linha);
        }
    }

    function abrirNovo() {
        editandoId = null;
        formResponsavel.reset();
        modalMsg.hidden = true;
        modalTitulo.textContent = 'Novo responsável';
        abrirModal(modal);
    }

    function abrirEdicao(item) {
        editandoId = item.id;
        modalMsg.hidden = true;
        modalTitulo.textContent = 'Editar responsável';
        formResponsavel.nome.value = item.usuario?.nome || '';
        formResponsavel.email.value = item.usuario?.email || '';
        formResponsavel.cpf.value = item.usuario?.cpf || '';
        formResponsavel.telefone.value = item.usuario?.telefone || '';
        formResponsavel.dataNascimento.value = item.usuario?.dataNascimento || '';
        formResponsavel.parentesco.value = item.parentesco || 'PAI';
        abrirModal(modal);
    }

    async function excluir(item) {
        if (!confirm(`Excluir o responsável "${item.usuario?.nome}"?`)) return;
        try {
            await responsaveisApi.excluir(item.id);
            notificar('Responsável excluído.', 'success');
            carregar(paginaAtual);
        } catch (erro) {
            notificar(erro.message, 'error');
        }
    }

    formResponsavel.addEventListener('submit', async (evento) => {
        evento.preventDefault();
        modalMsg.hidden = true;
        const dados = coletarFormulario(formResponsavel);
        try {
            if (editandoId) {
                await responsaveisApi.atualizar(editandoId, dados);
                notificar('Responsável atualizado.', 'success');
            } else {
                await responsaveisApi.criar(dados);
                notificar('Responsável criado. Senha temporária enviada por e-mail (ver log do servidor).', 'success');
            }
            fecharModal(modal);
            carregar(editandoId ? paginaAtual : 0);
        } catch (erro) {
            modalMsg.textContent = erro.message;
            modalMsg.hidden = false;
        }
    });

    formFiltro.addEventListener('submit', (evento) => {
        evento.preventDefault();
        buscaAtual = formFiltro.busca.value;
        filtrarLinhas(tbody, buscaAtual);
    });

    raiz.querySelector('#btnNovo').addEventListener('click', abrirNovo);
    raiz.querySelector('#btnFechar').addEventListener('click', () => fecharModal(modal));
    raiz.querySelector('#btnCancelar').addEventListener('click', () => fecharModal(modal));

    carregar(0);
}

