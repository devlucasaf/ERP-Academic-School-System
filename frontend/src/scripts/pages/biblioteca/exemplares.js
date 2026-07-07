import { livroApi } from '../../remotes/biblioteca/livros.js';
import { exemplarApi } from '../../remotes/biblioteca/exemplares.js';
import { renderHeader, badge, formatDate } from './_shared.js';

renderHeader('Biblioteca - Exemplares');
const app = document.getElementById('app');

let livroSelecionado = null;

app.innerHTML = `
    <section class="card">
        <h2>Selecionar livro</h2>
        <form id="formBuscaLivro" class="toolbar">
            <label>Título<input name="titulo" required /></label>
            <button type="submit">Buscar</button>
        </form>
        <div id="resultadosLivro"></div>
    </section>
    <section class="card" id="cardExemplares" style="display:none">
        <h2>Exemplares de <span id="tituloLivro"></span></h2>
        <form id="formExemplar" class="toolbar">
            <label>Código de barras
                <input name="codigoBarras" placeholder="deixe vazio para gerar" />
            </label>
            <label>Localização<input name="localizacao" placeholder="ex.: Estante A3" /></label>
            <button type="button" id="btnGerar" class="secondary">Gerar código</button>
            <button type="submit">Adicionar exemplar</button>
            <span id="msgExemplar"></span>
        </form>
        <table>
            <thead><tr><th>Código</th><th>Localização</th><th>Status</th><th>Criado</th><th></th></tr></thead>
            <tbody id="tbodyEx"></tbody>
        </table>
    </section>
`;

document.getElementById('formBuscaLivro').addEventListener('submit', async (e) => {
    e.preventDefault();
    const titulo = e.target.titulo.value;
    const box = document.getElementById('resultadosLivro');
    try {
        const page = await livroApi.buscar({ titulo, size: 10 });
        const livros = page.content || [];
        box.innerHTML = livros.length
            ? `<ul>${livros.map(l => `<li><a href="#" data-livro="${l.id}" data-titulo="${l.titulo}">${l.titulo} — ${l.autor}</a></li>`).join('')}</ul>`
            : '<p>Nenhum livro encontrado.</p>';
        box.querySelectorAll('[data-livro]').forEach(a => a.addEventListener('click', (ev) => {
            ev.preventDefault();
            livroSelecionado = { id: a.dataset.livro, titulo: a.dataset.titulo };
            document.getElementById('tituloLivro').textContent = livroSelecionado.titulo;
            document.getElementById('cardExemplares').style.display = 'block';
            listarExemplares();
        }));
    } catch (err) { box.innerHTML = `<p class="msg-error">${err.message}</p>`; }
});

document.getElementById('btnGerar').addEventListener('click', async () => {
    const r = await exemplarApi.gerarCodigoBarras();
    document.querySelector('#formExemplar [name=codigoBarras]').value = r.codigoBarras;
});

document.getElementById('formExemplar').addEventListener('submit', async (e) => {
    e.preventDefault();
    const msg = document.getElementById('msgExemplar');
    try {
        await exemplarApi.criar({
            livroId: livroSelecionado.id,
            codigoBarras: e.target.codigoBarras.value || null,
            localizacao: e.target.localizacao.value || null
        });
        e.target.reset();
        msg.innerHTML = '<span class="msg-ok">Exemplar criado.</span>';
        listarExemplares();
    } catch (err) { msg.innerHTML = `<span class="msg-error">${err.message}</span>`; }
});

async function listarExemplares() {
    const page = await exemplarApi.listarPorLivro(livroSelecionado.id, { size: 50 });
    const rows = (page.content || []).map(e => `
        <tr>
            <td>${e.codigoBarras}</td><td>${e.localizacao || '-'}</td>
            <td>${badge(e.status)}</td><td>${formatDate(e.criadoEm)}</td>
            <td><button data-del="${e.id}" class="danger">Excluir</button></td>
        </tr>`).join('');
    document.getElementById('tbodyEx').innerHTML = rows || '<tr><td colspan="5">Sem exemplares.</td></tr>';
    document.querySelectorAll('[data-del]').forEach(b => b.addEventListener('click', async () => {
        if (!confirm('Excluir exemplar?')) return;
        try { await exemplarApi.deletar(b.dataset.del); listarExemplares(); }
        catch (err) { alert(err.message); }
    }));
}

