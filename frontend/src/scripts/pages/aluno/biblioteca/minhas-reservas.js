import { reservaApi } from '../../../remotes/biblioteca/reservas.js';
import { renderHeader, badge, formatDate } from '../../biblioteca/_shared.js';

renderHeader('Portal do Aluno - Minhas reservas', 'aluno');
const app = document.getElementById('app');

const usuarioId = localStorage.getItem('usuarioId');
if (!usuarioId) {
    app.innerHTML = `<p class="msg-error">Faça login para visualizar suas reservas.</p>`;
} else {
    app.innerHTML = `
        <section class="card">
            <h2>Minhas reservas</h2>
            <table>
                <thead><tr><th>Livro</th><th>Data</th><th>Status</th><th>Posição fila</th><th></th></tr></thead>
                <tbody id="tbody"></tbody>
            </table>
        </section>
    `;

    async function carregar() {
        const lista = await reservaApi.doUsuario(usuarioId);
        document.getElementById('tbody').innerHTML = lista.map(r => `
            <tr>
                <td>${r.livroTitulo}</td>
                <td>${formatDate(r.dataReserva)}</td>
                <td>${badge(r.status)}</td>
                <td>${r.posicaoFila}</td>
                <td>${r.status === 'AGUARDANDO'
                    ? `<button data-cancel="${r.id}" class="danger">Cancelar</button>` : ''}</td>
            </tr>`).join('') || '<tr><td colspan="5">Nenhuma reserva.</td></tr>';
        document.querySelectorAll('[data-cancel]').forEach(b => b.addEventListener('click', async () => {
            if (!confirm('Cancelar reserva?')) return;
            try { await reservaApi.cancelar(b.dataset.cancel); carregar(); }
            catch (err) { alert(err.message); }
        }));
    }
    carregar();
}

