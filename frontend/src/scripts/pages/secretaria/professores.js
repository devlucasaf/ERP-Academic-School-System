// --- TELA DE PROFESSORES (LISTAGEM, MODAL DE CRIAR/EDITAR, EXCLUSÃO) ---
import { professoresApi } from '../../remotes/professores/index.js';
import {
    notificar, formatarData, coletarFormulario,
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
    const formProfessor = raiz.querySelector('#formProfessor');

    let paginaAtual = 0;
    let buscaAtual = '';
    let editandoId = null;

    // --- CARREGA A PÁGINA ATUAL ---
    async function carregar(pagina = 0) {
        paginaAtual = pagina;
        try {
            const page = await professoresApi.listar({ page: pagina, size: 10, sort: 'dataAdmissao,desc' });
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
            linha.querySelector('[data-campo="formacao"]').textContent = item.formacao || '-';
            linha.querySelector('[data-campo="area"]').textContent = item.areaAtuacao || '-';
            linha.querySelector('[data-campo="admissao"]').textContent = formatarData(item.dataAdmissao);
            linha.querySelector('[data-campo="ativo"]').textContent = item.ativo ? 'Sim' : 'Não';
            linha.querySelector('[data-acao="editar"]').addEventListener('click', () => abrirEdicao(item));
            linha.querySelector('[data-acao="excluir"]').addEventListener('click', () => excluir(item));
            tbody.appendChild(linha);
        }
    }

    function abrirNovo() {
        editandoId = null;
        formProfessor.reset();
        formProfessor.ativo.checked = true;
        modalMsg.hidden = true;
        modalTitulo.textContent = 'Novo professor';
        abrirModal(modal);
    }

    function abrirEdicao(item) {
        editandoId = item.id;
        modalMsg.hidden = true;
        modalTitulo.textContent = 'Editar professor';
        formProfessor.nome.value = item.usuario?.nome || '';
        formProfessor.email.value = item.usuario?.email || '';
        formProfessor.cpf.value = item.usuario?.cpf || '';
        formProfessor.telefone.value = item.usuario?.telefone || '';
        formProfessor.dataNascimento.value = item.usuario?.dataNascimento || '';
        formProfessor.formacao.value = item.formacao || '';
        formProfessor.areaAtuacao.value = item.areaAtuacao || '';
        formProfessor.cargaHorariaSemanal.value = item.cargaHorariaSemanal ?? '';
        formProfessor.dataAdmissao.value = item.dataAdmissao || '';
        formProfessor.ativo.checked = item.ativo !== false;
        abrirModal(modal);
    }

    async function excluir(item) {
        if (!confirm(`Excluir o professor "${item.usuario?.nome}"?`)) return;
        try {
            await professoresApi.excluir(item.id);
            notificar('Professor excluído.', 'success');
            carregar(paginaAtual);
        } catch (erro) {
            notificar(erro.message, 'error');
        }
    }

    formProfessor.addEventListener('submit', async (evento) => {
        evento.preventDefault();
        modalMsg.hidden = true;
        const dados = coletarFormulario(formProfessor);
        dados.ativo = formProfessor.ativo.checked;
        dados.cargaHorariaSemanal = dados.cargaHorariaSemanal ? Number(dados.cargaHorariaSemanal) : null;
        try {
            if (editandoId) {
                await professoresApi.atualizar(editandoId, dados);
                notificar('Professor atualizado.', 'success');
            } else {
                await professoresApi.criar(dados);
                notificar('Professor criado. Senha temporária enviada por e-mail (ver log do servidor).', 'success');
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

