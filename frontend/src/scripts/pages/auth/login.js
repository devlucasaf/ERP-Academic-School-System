// --- LÓGICA DA TELA DE LOGIN ---
import { login, dashboardForRole } from '../../auth.js';
import { toast } from '../../util.js';

// --- MONTA A TELA: LIGA O SUBMIT DO FORMULÁRIO ---
export function mount(root) {
    const form = root.querySelector('#formLogin');
    const msg = root.querySelector('#loginMsg');
    const btn = root.querySelector('#btnEntrar');

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        msg.hidden = true;

        const email = form.email.value.trim();
        const senha = form.senha.value;

        // --- VALIDAÇÃO SIMPLES ---
        if (!email || !senha) {
            msg.textContent = 'Informe e-mail e senha.';
            msg.hidden = false;
            return;
        }

        btn.disabled = true;
        btn.textContent = 'Entrando...';
        try {
            // --- AUTENTICA E REDIRECIONA CONFORME A ROLE ---
            const usuario = await login(email, senha);
            toast(`Bem-vindo, ${usuario.nome}!`, 'success');
            location.hash = dashboardForRole(usuario.role);
        } catch (err) {
            msg.textContent = err.message;
            msg.hidden = false;
            toast(err.message, 'error');
        } finally {
            btn.disabled = false;
            btn.textContent = 'Entrar';
        }
    });
}

