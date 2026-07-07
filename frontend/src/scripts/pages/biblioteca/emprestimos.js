import { emprestimoApi } from '../../remotes/biblioteca/emprestimos.js';
import { exemplarApi } from '../../remotes/biblioteca/exemplares.js';
import { renderHeader, badge, formatDate, money } from './_shared.js';

renderHeader('Biblioteca - Empréstimos');
const app = document.getElementById('app');

app.innerHTML = `
    <section class="card">
        <h2>Novo empréstimo</h2>
        <form id="formEmp" class="toolbar">
            <label>Código de barras do exemplar<input name="codigoBarras" required autofocus /></label>
            <label>ID do usuário (aluno/professor)<input name="usuarioId" required /></label>
            <button type="submit">Registrar</button>
            <span id="msg"></span>
        </form>
    </section>
    <section class="card">
        <h2>Consultar empréstimos por usuário</h2>
        <form id="formCons" class="toolbar">
            <label>ID do usuário<input name="usuarioId" required /></label>
            <button type="submit">Listar</button>
        </form>
        <table>
            <thead><tr>
                <th>Livro</th><th>Código</th><th>Empréstimo</th><th>Prev. devolução</th>
                <th>Devolvido em</th><th>Status</th><th>Renovações</th><th>Multa</th>
            </tr></thead>
            <tbody id="tbody"></tbody>
        </table>
    </section>
`;

document.getElementById('formEmp').addEventListener('submit', async (e) => {
    e.preventDefault();
    const msg = document.getElementById('msg');
    try {
        // --- BUSCA EXEMPLAR PARA CONVERTER O CÓDIGO EM ID ---
        const ex = await exemplarApi.buscarPorCodigo(e.target.codigoBarras.value.trim());
        const emp = await emprestimoApi.registrar({
            exemplarId: ex.id,
            usuarioId: e.target.usuarioId.value.trim()
        });
        msg.innerHTML = `<span class="msg-ok">Empréstimo #${emp.id.substring(0,8)} registrado. Devolução prevista: ${formatDate(emp.dataDevolucaoPrevista)}</span>`;
        e.target.reset();
    } catch (err) { msg.innerHTML = `<span class="msg-error">${err.message}</span>`; }
});

document.getElementById('formCons').addEventListener('submit', async (e) => {
    e.preventDefault();
    const usuarioId = e.target.usuarioId.value.trim();
    const page = await emprestimoApi.listarPorUsuario(usuarioId, { size: 50, sort: 'dataEmprestimo,desc' });
    const rows = (page.content || []).map(emp => `
        <tr>
            <td>${emp.livroTitulo}</td>
            <td>${emp.exemplarCodigoBarras}</td>
            <td>${formatDate(emp.dataEmprestimo)}</td>
            <td>${formatDate(emp.dataDevolucaoPrevista)}</td>
            <td>${formatDate(emp.dataDevolucaoEfetiva)}</td>
            <td>${badge(emp.status)}</td>
            <td>${emp.renovacoes}</td>
            <td>${money(emp.valorMulta)}</td>
        </tr>`).join('');
    document.getElementById('tbody').innerHTML = rows || '<tr><td colspan="8">Nenhum empréstimo.</td></tr>';
});

