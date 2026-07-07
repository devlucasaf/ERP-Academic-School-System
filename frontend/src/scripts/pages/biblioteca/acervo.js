import { livroApi } from '../../remotes/biblioteca/livros.js';
import { renderHeader, formatDate } from './_shared.js';

renderHeader('Biblioteca - Acervo');
const app = document.getElementById('app');

let livroEmEdicao = null;

async function listar(params = {}) {
    try {
        const page = await livroApi.buscar({ size: 20, ...params });
        renderLista(page.content || page || []);
    } catch (e) {
        renderLista([], e.message);
    }
}

function renderLista(livros, erro) {
    app.innerHTML = `
        <section class="card">
            <h2>Cadastro de Livros</h2>
            <form id="formLivro" class="grid">
                <label>Título<input name="titulo" required /></label>
                <label>Autor<input name="autor" required /></label>
                <label>ISBN<input name="isbn" /></label>
                <label>Editora<input name="editora" /></label>
                <label>Ano<input name="anoPublicacao" type="number" /></label>
                <label>Edição<input name="edicao" /></label>
                <label>Páginas<input name="paginas" type="number" /></label>
                <label>Categoria<input name="categoria" /></label>
                <label>Capa (imagem)<input name="capa" type="file" accept="image/*" /></label>
                <label>Sinopse<textarea name="sinopse"></textarea></label>
                <div class="toolbar" style="grid-column:1/-1">
                    <button type="submit">Salvar</button>
                    <button type="button" id="btnLimpar" class="secondary">Limpar</button>
                    <span id="formMsg"></span>
                </div>
            </form>
        </section>

        <section class="card">
            <h2>Buscar</h2>
            <form id="formBusca" class="toolbar">
                <label>Título<input name="titulo" /></label>
                <label>Autor<input name="autor" /></label>
                <label>Categoria<input name="categoria" /></label>
                <label>ISBN<input name="isbn" /></label>
                <button type="submit">Filtrar</button>
                <button type="button" id="btnLimparBusca" class="secondary">Limpar</button>
            </form>
        </section>

        <section class="card">
            <h2>Resultados</h2>
            ${erro ? `<p class="msg-error">${erro}</p>` : ''}
            <table>
                <thead>
                    <tr>
                        <th>Título</th><th>Autor</th><th>Categoria</th>
                        <th>ISBN</th><th>Exemplares</th><th>Criado</th><th></th>
                    </tr>
                </thead>
                <tbody>
                    ${livros.map(l => `
                        <tr>
                            <td>${l.titulo}</td>
                            <td>${l.autor}</td>
                            <td>${l.categoria || '-'}</td>
                            <td>${l.isbn || '-'}</td>
                            <td>${l.exemplaresDisponiveis}/${l.totalExemplares}</td>
                            <td>${formatDate(l.criadoEm)}</td>
                            <td>
                                <button data-edit="${l.id}" class="secondary">Editar</button>
                                <button data-del="${l.id}" class="danger">Excluir</button>
                            </td>
                        </tr>
                    `).join('') || '<tr><td colspan="7">Nenhum livro encontrado.</td></tr>'}
                </tbody>
            </table>
        </section>
    `;

    if (livroEmEdicao) preencherForm(livroEmEdicao);

    document.getElementById('formLivro').addEventListener('submit', onSalvar);
    document.getElementById('btnLimpar').addEventListener('click', () => {
        livroEmEdicao = null;
        document.getElementById('formLivro').reset();
    });
    document.getElementById('formBusca').addEventListener('submit', (e) => {
        e.preventDefault();
        const f = new FormData(e.target);
        const params = {};
        for (const [k, v] of f.entries()) if (v) params[k] = v;
        listar(params);
    });
    document.getElementById('btnLimparBusca').addEventListener('click', () => listar());
    app.querySelectorAll('[data-edit]').forEach(btn =>
        btn.addEventListener('click', () => onEditar(btn.dataset.edit, livros)));
    app.querySelectorAll('[data-del]').forEach(btn =>
        btn.addEventListener('click', () => onExcluir(btn.dataset.del)));
}

function preencherForm(l) {
    const f = document.getElementById('formLivro');
    ['titulo','autor','isbn','editora','anoPublicacao','edicao','paginas','categoria','sinopse']
        .forEach(k => { if (f[k]) f[k].value = l[k] ?? ''; });
}

function onEditar(id, livros) {
    livroEmEdicao = livros.find(l => l.id === id);
    preencherForm(livroEmEdicao);
    window.scrollTo(0, 0);
}

async function onExcluir(id) {
    if (!confirm('Confirmar exclusão do livro?')) return;
    try {
        await livroApi.deletar(id);
        livroEmEdicao = null;
        listar();
    } catch (e) { alert(e.message); }
}

async function onSalvar(e) {
    e.preventDefault();
    const f = e.target;
    const msg = document.getElementById('formMsg');
    const dados = {
        titulo: f.titulo.value,
        autor: f.autor.value,
        isbn: f.isbn.value || null,
        editora: f.editora.value || null,
        anoPublicacao: f.anoPublicacao.value ? Number(f.anoPublicacao.value) : null,
        edicao: f.edicao.value || null,
        paginas: f.paginas.value ? Number(f.paginas.value) : null,
        categoria: f.categoria.value || null,
        sinopse: f.sinopse.value || null
    };
    const capa = f.capa.files[0];
    try {
        if (livroEmEdicao) {
            await livroApi.atualizar(livroEmEdicao.id, dados, capa);
            msg.innerHTML = '<span class="msg-ok">Atualizado.</span>';
        } else {
            await livroApi.criar(dados, capa);
            msg.innerHTML = '<span class="msg-ok">Cadastrado.</span>';
        }
        livroEmEdicao = null;
        listar();
    } catch (err) {
        msg.innerHTML = `<span class="msg-error">${err.message}</span>`;
    }
}

listar();

