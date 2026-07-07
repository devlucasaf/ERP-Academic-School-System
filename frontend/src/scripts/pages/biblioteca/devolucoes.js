import { emprestimoApi } from '../../remotes/biblioteca/emprestimos.js';
import { renderHeader, badge, formatDate, money } from './_shared.js';

renderHeader('Biblioteca - Devoluções');
const app = document.getElementById('app');

app.innerHTML = `
    <section class="card">
        <h2>Registrar devolução</h2>
        <form id="form" class="toolbar">
            <label>ID do empréstimo<input name="empId" required autofocus /></label>
            <button type="submit">Devolver</button>
            <button type="button" id="btnRen" class="secondary">Renovar</button>
            <span id="msg"></span>
        </form>
        <div id="detalhe"></div>
    </section>
`;

async function processar(acao) {
    const empId = document.querySelector('#form [name=empId]').value.trim();
    const msg = document.getElementById('msg');
    try {
        const r = acao === 'devolver'
            ? await emprestimoApi.devolver(empId)
            : await emprestimoApi.renovar(empId);
        msg.innerHTML = '<span class="msg-ok">OK</span>';
        document.getElementById('detalhe').innerHTML = `
            <table style="margin-top:1rem">
                <tr><th>Livro</th><td>${r.livroTitulo}</td></tr>
                <tr><th>Exemplar</th><td>${r.exemplarCodigoBarras}</td></tr>
                <tr><th>Usuário</th><td>${r.usuarioNome}</td></tr>
                <tr><th>Status</th><td>${badge(r.status)}</td></tr>
                <tr><th>Prev. devolução</th><td>${formatDate(r.dataDevolucaoPrevista)}</td></tr>
                <tr><th>Devolvido em</th><td>${formatDate(r.dataDevolucaoEfetiva)}</td></tr>
                <tr><th>Dias de atraso</th><td>${r.diasAtraso}</td></tr>
                <tr><th>Multa</th><td>${money(r.valorMulta)}</td></tr>
                <tr><th>Renovações</th><td>${r.renovacoes}</td></tr>
            </table>`;
    } catch (err) { msg.innerHTML = `<span class="msg-error">${err.message}</span>`; }
}

document.getElementById('form').addEventListener('submit', (e) => { e.preventDefault(); processar('devolver'); });
document.getElementById('btnRen').addEventListener('click', () => processar('renovar'));

