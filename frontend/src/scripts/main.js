console.log('ERP Academic School System - frontend inicializado');

const main = document.getElementById('main-content');
if (main) {
    main.innerHTML = `
        <p>Bem-vindo ao ERP Academic School System.</p>
        <section style="margin-top:1rem">
            <h2>Módulo Biblioteca</h2>
            <ul>
                <li><strong>Portal Biblioteca:</strong>
                    <a href="/src/scripts/pages/biblioteca/acervo.html">Acervo</a> ·
                    <a href="/src/scripts/pages/biblioteca/exemplares.html">Exemplares</a> ·
                    <a href="/src/scripts/pages/biblioteca/emprestimos.html">Empréstimos</a> ·
                    <a href="/src/scripts/pages/biblioteca/devolucoes.html">Devoluções</a> ·
                    <a href="/src/scripts/pages/biblioteca/reservas.html">Reservas</a> ·
                    <a href="/src/scripts/pages/biblioteca/multas.html">Multas</a> ·
                    <a href="/src/scripts/pages/biblioteca/configuracoes.html">Configurações</a>
                </li>
                <li><strong>Portal do Aluno:</strong>
                    <a href="/src/scripts/pages/aluno/biblioteca/consultar.html">Consultar</a> ·
                    <a href="/src/scripts/pages/aluno/biblioteca/meus-emprestimos.html">Meus empréstimos</a> ·
                    <a href="/src/scripts/pages/aluno/biblioteca/minhas-reservas.html">Minhas reservas</a>
                </li>
            </ul>
        </section>`;
}
