// --- TELA DE ALUNOS (LISTAGEM, FILTROS, MODAL DE CRIAR/EDITAR, EXCLUSÃO) ---
import { alunosApi } from '../../remotes/alunos/index.js';
import {
    notificar, formatarData, coletarFormulario,
    renderPaginacao, abrirModal, fecharModal, filtrarLinhas
} from './_comum.js';

export function montar(raiz) {
    // --- REFERÊNCIAS DE ELEMENTOS ---
    const tbody = raiz.querySelector('#tbody');
    const paginacao = raiz.querySelector('#paginacao');
    const tplLinha = raiz.querySelector('#linha');
    const tplVazia = raiz.querySelector('#linhaVazia');
    const formFiltro = raiz.querySelector('#formFiltro');
    const modal = raiz.querySelector('#modal');
    const modalTitulo = raiz.querySelector('#modalTitulo');
    const modalMsg = raiz.querySelector('#modalMsg');
    const formAluno = raiz.querySelector('#formAluno');

    let paginaAtual = 0;
    let statusAtual = '';
    let buscaAtual = '';
    let editandoId = null;

    // --- CARREGA A PÁGINA ATUAL ---
    async function carregar(pagina = 0) {
        paginaAtual = pagina;
        try {
            const page = await alunosApi.listar({ page: pagina, size: 10, sort: 'matriculaRA,asc', status: statusAtual });
            renderTabela(page.content || []);
            renderPaginacao(paginacao, page, carregar);
            filtrarLinhas(tbody, buscaAtual);
        } catch (erro) {
            notificar(erro.message, 'error');
        }
    }

    // --- RENDERIZA AS LINHAS ---
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
            linha.querySelector('[data-campo="ra"]').textContent = item.matriculaRA;
            linha.querySelector('[data-campo="status"]').textContent = item.status;
            linha.querySelector('[data-campo="ingresso"]').textContent = formatarData(item.dataIngresso);
            linha.querySelector('[data-acao="editar"]').addEventListener('click', () => abrirEdicao(item));
            linha.querySelector('[data-acao="excluir"]').addEventListener('click', () => excluir(item));
            tbody.appendChild(linha);
        }
    }

    // --- ABRE O MODAL PARA NOVO CADASTRO ---
    function abrirNovo() {
        editandoId = null;
        formAluno.reset();
        modalMsg.hidden = true;
        modalTitulo.textContent = 'Novo aluno';
        abrirModal(modal);
    }

    // --- ABRE O MODAL PARA EDIÇÃO ---
    function abrirEdicao(item) {
        editandoId = item.id;
        modalMsg.hidden = true;
        modalTitulo.textContent = 'Editar aluno';
        formAluno.nome.value = item.usuario?.nome || '';
        formAluno.email.value = item.usuario?.email || '';
        formAluno.cpf.value = item.usuario?.cpf || '';
        formAluno.telefone.value = item.usuario?.telefone || '';
        formAluno.dataNascimento.value = item.usuario?.dataNascimento || '';
        formAluno.matriculaRA.value = item.matriculaRA || '';
        formAluno.dataIngresso.value = item.dataIngresso || '';
        formAluno.status.value = item.status || 'ATIVO';
        formAluno.observacoes.value = item.observacoes || '';
        abrirModal(modal);
    }

    // --- EXCLUI UM ALUNO ---
    async function excluir(item) {
        if (!confirm(`Excluir o aluno "${item.usuario?.nome}"?`)) return;
        try {
            await alunosApi.excluir(item.id);
            notificar('Aluno excluído.', 'success');
            carregar(paginaAtual);
        } catch (erro) {
            notificar(erro.message, 'error');
        }
    }

    // --- SALVA (CRIA OU ATUALIZA) ---
    formAluno.addEventListener('submit', async (evento) => {
        evento.preventDefault();
        modalMsg.hidden = true;
        const dados = coletarFormulario(formAluno);
        try {
            if (editandoId) {
                await alunosApi.atualizar(editandoId, dados);
                notificar('Aluno atualizado.', 'success');
            } else {
                await alunosApi.criar(dados);
                notificar('Aluno criado. Senha temporária enviada por e-mail (ver log do servidor).', 'success');
            }
            fecharModal(modal);
            carregar(editandoId ? paginaAtual : 0);
        } catch (erro) {
            modalMsg.textContent = erro.message;
            modalMsg.hidden = false;
        }
    });

    // --- FILTROS ---
    formFiltro.addEventListener('submit', (evento) => {
        evento.preventDefault();
        statusAtual = formFiltro.status.value;
        buscaAtual = formFiltro.busca.value;
        carregar(0);
    });

    // --- EVENTOS DO MODAL ---
    raiz.querySelector('#btnNovo').addEventListener('click', abrirNovo);
    raiz.querySelector('#btnFechar').addEventListener('click', () => fecharModal(modal));
    raiz.querySelector('#btnCancelar').addEventListener('click', () => fecharModal(modal));

    // --- CARGA INICIAL ---
    carregar(0);
}

