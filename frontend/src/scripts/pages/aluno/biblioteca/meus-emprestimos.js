import { emprestimoApi } from '../../../remotes/biblioteca/emprestimos.js';
import { multaApi } from '../../../remotes/biblioteca/multas.js';
import { renderHeader, badge, formatDate, money } from '../../biblioteca/_shared.js';

renderHeader('Portal do Aluno - Meus empréstimos', 'aluno');
const app = document.getElementById('app');

// --- ID DO USUÁRIO AUTENTICADO É LIDO DE LOCALSTORAGE (SALVO NO LOGIN) ---
const usuarioId = localStorage.getItem('usuarioId');

if (!usuarioId) {
    app.innerHTML = `<p class="msg-error">Faça login para visualizar seus empréstimos.</p>`;
} else {
    app.innerHTML = `
        <section class="card">
            <h2>Meus empréstimos</h2>
            <table>
                <thead><tr>
                    <th>Livro</th><th>Empréstimo</th><th>Devolução prevista</th>
                    <th>Devolvido em</th><th>Status</th><th>Renovações</th><th></th>
                </tr></thead>
                <tbody id="tbodyEmp"></tbody>
            </table>
        </section>
        <section class="card">
            <h2>Minhas multas pendentes</h2>
            <table>
                <thead><tr><th>Livro</th><th>Dias atraso</th><th>Valor</th><th>Gerada em</th></tr></thead>
                <tbody id="tbodyMulta"></tbody>
            </table>
        </section>
    `;

    (async () => {
        const page = await emprestimoApi.listarPorUsuario(usuarioId, { size: 100, sort: 'dataEmprestimo,desc' });
        document.getElementById('tbodyEmp').innerHTML = (page.content || []).map(e => `
            <tr>
                <td>${e.livroTitulo}</td>
                <td>${formatDate(e.dataEmprestimo)}</td>
                <td>${formatDate(e.dataDevolucaoPrevista)}</td>
                <td>${formatDate(e.dataDevolucaoEfetiva)}</td>
                <td>${badge(e.status)}</td>
                <td>${e.renovacoes}</td>
                <td>${e.status !== 'DEVOLVIDO'
                    ? `<button data-renovar="${e.id}">Renovar</button>` : ''}</td>
            </tr>`).join('') || '<tr><td colspan="7">Nenhum empréstimo.</td></tr>';

        document.querySelectorAll('[data-renovar]').forEach(b => b.addEventListener('click', async () => {
            try { await emprestimoApi.renovar(b.dataset.renovar); location.reload(); }
            catch (err) { alert(err.message); }
        }));

        const multas = await multaApi.pendentesDoUsuario(usuarioId);
        document.getElementById('tbodyMulta').innerHTML = multas.map(m => `
            <tr><td>${m.livroTitulo}</td><td>${m.diasAtraso}</td>
                <td>${money(m.valor)}</td><td>${formatDate(m.geradaEm)}</td></tr>`
        ).join('') || '<tr><td colspan="4">Sem multas pendentes.</td></tr>';
    })();
}

