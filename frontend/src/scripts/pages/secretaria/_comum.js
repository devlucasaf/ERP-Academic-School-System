// --- HELPERS COMPARTILHADOS PELAS TELAS DE CADASTRO DA SECRETARIA ---
import { notificar, formatarData } from '../../util.js';

export { notificar, formatarData };

// --- COLETA OS CAMPOS DE UM FORMULÁRIO (VAZIO VIRA null) ---
export function coletarFormulario(formulario) {
    const dados = {};
    new FormData(formulario).forEach((valor, chave) => {
        dados[chave] = valor === '' ? null : valor;
    });
    return dados;
}

// --- RENDERIZA A BARRA DE PAGINAÇÃO A PARTIR DE UM Page DO SPRING ---
export function renderPaginacao(container, page, aoIr) {
    container.replaceChildren();

    const anterior = document.createElement('button');
    anterior.className = 'btn secondary btn-sm';
    anterior.textContent = '← Anterior';
    anterior.disabled = page.first;
    anterior.addEventListener('click', () => aoIr(page.number - 1));

    const info = document.createElement('span');
    info.textContent = `Página ${page.number + 1} de ${page.totalPages || 1} · ${page.totalElements} registro(s)`;

    const proxima = document.createElement('button');
    proxima.className = 'btn secondary btn-sm';
    proxima.textContent = 'Próxima →';
    proxima.disabled = page.last;
    proxima.addEventListener('click', () => aoIr(page.number + 1));

    container.append(anterior, info, proxima);
}

// --- ABRE/FECHA O MODAL ---
export function abrirModal(overlay) {
    overlay.hidden = false;
}

export function fecharModal(overlay) {
    overlay.hidden = true;
}

// --- FILTRA AS LINHAS VISÍVEIS DA TABELA POR UM TEXTO (BUSCA LOCAL NA PÁGINA) ---
export function filtrarLinhas(tbody, texto) {
    const termo = (texto || '').trim().toLowerCase();
    tbody.querySelectorAll('tr').forEach(linha => {
        linha.hidden = termo && !linha.textContent.toLowerCase().includes(termo);
    });
}

