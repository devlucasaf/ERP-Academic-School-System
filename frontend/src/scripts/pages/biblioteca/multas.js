import { multaApi } from '../../remotes/biblioteca/multas.js';
import { renderHeader, badge, formatDate, money } from './_shared.js';

renderHeader('Biblioteca - Multas');
const app = document.getElementById('app');

app.innerHTML = `
    <section class="card">
        <h2>Multas</h2>
        <form id="formFiltro" class="toolbar">
            <label>Status
                <select name="status">
                    <option value="PENDENTE" selected>PENDENTE</option>
                    <option value="PAGA">PAGA</option>
                    <option value="CANCELADA">CANCELADA</option>
                </select>
            </label>
            <button type="submit">Listar</button>
        </form>
        <table>
            <thead><tr>
                <th>Usuário</th><th>Livro</th><th>Dias atraso</th><th>Valor</th>
                <th>Gerada</th><th>Paga</th><th>Status</th><th></th>
            </tr></thead>
            <tbody id="tbody"></tbody>
        </table>
    </section>
`;

async function listar(status = 'PENDENTE') {
    const page = await multaApi.listar({ status, size: 50 });
    document.getElementById('tbody').innerHTML = (page.content || []).map(m => `
        <tr>
            <td>${m.usuarioNome}</td>
            <td>${m.livroTitulo}</td>
            <td>${m.diasAtraso}</td>
            <td>${money(m.valor)}</td>
            <td>${formatDate(m.geradaEm)}</td>
            <td>${formatDate(m.pagaEm)}</td>
            <td>${badge(m.status)}</td>
            <td>${m.status === 'PENDENTE'
                ? `<button data-pagar="${m.id}">Baixar pagto.</button>
                   <button data-cancel="${m.id}" class="danger">Cancelar</button>` : ''}</td>
        </tr>`).join('') || '<tr><td colspan="8">Nenhuma multa.</td></tr>';

    document.querySelectorAll('[data-pagar]').forEach(b => b.addEventListener('click', async () => {
        try { await multaApi.pagar(b.dataset.pagar); listar(status); }
        catch (err) { alert(err.message); }
    }));
    document.querySelectorAll('[data-cancel]').forEach(b => b.addEventListener('click', async () => {
        if (!confirm('Cancelar multa?')) return;
        try { await multaApi.cancelar(b.dataset.cancel); listar(status); }
        catch (err) { alert(err.message); }
    }));
}

document.getElementById('formFiltro').addEventListener('submit', (e) => {
    e.preventDefault();
    listar(e.target.status.value);
});
listar();

