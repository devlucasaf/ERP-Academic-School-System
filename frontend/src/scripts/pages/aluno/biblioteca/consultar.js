import { livroApi } from '../../../remotes/biblioteca/livros.js';
import { reservaApi } from '../../../remotes/biblioteca/reservas.js';
import { renderHeader } from '../../biblioteca/_shared.js';

renderHeader('Portal do Aluno - Biblioteca', 'aluno');
const app = document.getElementById('app');

app.innerHTML = `
    <section class="card">
        <h2>Consultar acervo</h2>
        <form id="form" class="toolbar">
            <label>Título<input name="titulo" /></label>
            <label>Autor<input name="autor" /></label>
            <label>Categoria<input name="categoria" /></label>
            <label>ISBN<input name="isbn" /></label>
            <button type="submit">Buscar</button>
        </form>
    </section>
    <section class="card">
        <div id="resultado"></div>
        <div id="paginacao" class="toolbar" style="margin-top:.75rem"></div>
    </section>
`;

let paginaAtual = 0;
let filtroAtual = {};

async function buscar() {
    const params = { ...filtroAtual, page: paginaAtual, size: 10 };
    const page = await livroApi.buscar(params);
    const livros = page.content || [];
    document.getElementById('resultado').innerHTML = `
        <table>
            <thead><tr><th>Título</th><th>Autor</th><th>Categoria</th><th>Disponíveis</th><th></th></tr></thead>
            <tbody>
                ${livros.map(l => `
                    <tr>
                        <td>${l.titulo}</td><td>${l.autor}</td><td>${l.categoria || '-'}</td>
                        <td>${l.exemplaresDisponiveis}/${l.totalExemplares}</td>
                        <td>${l.exemplaresDisponiveis === 0
                            ? `<button data-reservar="${l.id}">Reservar</button>`
                            : '<span class="badge DISPONIVEL">Disponível</span>'}</td>
                    </tr>`).join('') || '<tr><td colspan="5">Nada encontrado.</td></tr>'}
            </tbody>
        </table>
    `;
    document.getElementById('paginacao').innerHTML = `
        <button ${page.first ? 'disabled' : ''} id="prev" class="secondary">← Anterior</button>
        <span>Página ${page.number + 1} de ${page.totalPages || 1}</span>
        <button ${page.last ? 'disabled' : ''} id="next" class="secondary">Próxima →</button>
    `;
    document.getElementById('prev').addEventListener('click', () => { paginaAtual--; buscar(); });
    document.getElementById('next').addEventListener('click', () => { paginaAtual++; buscar(); });
    document.querySelectorAll('[data-reservar]').forEach(b => b.addEventListener('click', async () => {
        try {
            const r = await reservaApi.reservar({ livroId: b.dataset.reservar });
            alert(`Reservado! Posição na fila: ${r.posicaoFila}`);
        } catch (err) { alert(err.message); }
    }));
}

document.getElementById('form').addEventListener('submit', (e) => {
    e.preventDefault();
    filtroAtual = {};
    const f = new FormData(e.target);
    for (const [k, v] of f.entries()) if (v) filtroAtual[k] = v;
    paginaAtual = 0;
    buscar();
});

buscar();

