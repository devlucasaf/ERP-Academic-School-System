// --- FÁBRICA DE DASHBOARDS PLACEHOLDER (PREENCHE TÍTULO E BOAS-VINDAS) ---
import { getUser } from '../../auth.js';

export function makeDashboard(titulo) {
    return function mount(root) {
        const usuario = getUser();
        const tituloEl = root.querySelector('[data-cell="titulo"]');
        const welcomeEl = root.querySelector('[data-cell="welcome"]');
        if (tituloEl) tituloEl.textContent = titulo;
        if (welcomeEl) welcomeEl.textContent = `Bem-vindo, ${usuario?.nome || 'usuário'}!`;
    };
}

