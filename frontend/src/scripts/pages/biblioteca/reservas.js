import { reservaApi } from '../../remotes/biblioteca/reservas.js';
import { renderHeader, badge, formatDate } from './_shared.js';

renderHeader('Biblioteca - Reservas');
const app = document.getElementById('app');

app.innerHTML = `
    <section class="card">
        <h2>Fila de reservas de um livro</h2>
        <form id="formFila" class="toolbar">
            <label>ID do livro<input name="livroId" required /></label>
            <button type="submit">Ver fila</button>
        </form>
        <table>
            <thead><tr><th>Pos.</th><th>Usuário</th><th>Data</th><th>Status</th><th></th></tr></thead>
            <tbody id="tbody"></tbody>
        </table>
    </section>
`;

document.getElementById('formFila').addEventListener('submit', async (e) => {
    e.preventDefault();
    const lista = await reservaApi.filaDoLivro(e.target.livroId.value.trim());
    document.getElementById('tbody').innerHTML = lista.map(r => `
        <tr>
            <td>${r.posicaoFila}</td>
            <td>${r.usuarioNome}</td>
            <td>${formatDate(r.dataReserva)}</td>
            <td>${badge(r.status)}</td>
            <td><button data-cancel="${r.id}" class="danger">Cancelar</button></td>
        </tr>`).join('') || '<tr><td colspan="5">Fila vazia.</td></tr>';
    document.querySelectorAll('[data-cancel]').forEach(b => b.addEventListener('click', async () => {
        if (!confirm('Cancelar reserva?')) return;
        try { await reservaApi.cancelar(b.dataset.cancel);
            document.getElementById('formFila').dispatchEvent(new Event('submit')); }
        catch (err) { alert(err.message); }
    }));
});

