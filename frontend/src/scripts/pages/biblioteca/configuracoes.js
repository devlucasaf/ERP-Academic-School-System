import { configuracaoBibliotecaApi } from '../../remotes/biblioteca/configuracoes.js';
import { renderHeader } from './_shared.js';

renderHeader('Biblioteca - Configurações');
const app = document.getElementById('app');

async function carregar() {
    const c = await configuracaoBibliotecaApi.obter();
    app.innerHTML = `
        <section class="card">
            <h2>Parâmetros gerais</h2>
            <form id="form" class="grid">
                <label>Prazo empréstimo aluno (dias)
                    <input type="number" name="prazoEmprestimoAluno" value="${c.prazoEmprestimoAluno}" required min="1" />
                </label>
                <label>Prazo empréstimo professor (dias)
                    <input type="number" name="prazoEmprestimoProfessor" value="${c.prazoEmprestimoProfessor}" required min="1" />
                </label>
                <label>Máx. empréstimos simultâneos
                    <input type="number" name="maxEmprestimosSimultaneos" value="${c.maxEmprestimosSimultaneos}" required min="1" />
                </label>
                <label>Máx. renovações
                    <input type="number" name="maxRenovacoes" value="${c.maxRenovacoes}" required min="0" />
                </label>
                <label>Valor da multa por dia (R$)
                    <input type="number" step="0.01" name="valorMultaDia" value="${c.valorMultaDia}" required min="0" />
                </label>
                <div class="toolbar" style="grid-column:1/-1">
                    <button type="submit">Salvar</button>
                    <span id="msg"></span>
                </div>
            </form>
        </section>`;
    document.getElementById('form').addEventListener('submit', async (e) => {
        e.preventDefault();
        const f = e.target;
        try {
            await configuracaoBibliotecaApi.atualizar({
                prazoEmprestimoAluno: Number(f.prazoEmprestimoAluno.value),
                prazoEmprestimoProfessor: Number(f.prazoEmprestimoProfessor.value),
                maxEmprestimosSimultaneos: Number(f.maxEmprestimosSimultaneos.value),
                maxRenovacoes: Number(f.maxRenovacoes.value),
                valorMultaDia: Number(f.valorMultaDia.value)
            });
            document.getElementById('msg').innerHTML = '<span class="msg-ok">Salvo.</span>';
        } catch (err) { document.getElementById('msg').innerHTML = `<span class="msg-error">${err.message}</span>`; }
    });
}
carregar();

