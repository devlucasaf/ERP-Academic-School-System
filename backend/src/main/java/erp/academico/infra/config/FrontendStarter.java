package erp.academico.infra.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

@Component
@ConditionalOnProperty(name = "app.dev.start-frontend", havingValue = "true", matchIfMissing = true)
public class FrontendStarter {

    private static final String FRONTEND_DIR = "../frontend";
    private static final String FRONTEND_URL = "http://localhost:5173";
    private static final String SWAGGER_URL  = "http://localhost:8080/api/swagger-ui.html";
    private static final String BACKEND_URL  = "http://localhost:8080/api";

    // --- EXECUTA ASSIM QUE A APLICACAO FICA PRONTA ---
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        System.out.println();
        System.out.println("=================================================================");
        System.out.println("  ERP Sistema Acadêmico - Iniciando ambiente de desenvolvimento...");
        System.out.println("=================================================================");

        // --- INICIA O FRONTEND EM UMA THREAD SEPARADA PARA NAO BLOQUEAR O SPRING ---
        new Thread(() -> {
            try {
                if (isViteRodando()) {
                    System.out.println(" Frontend ja esta rodando em " + FRONTEND_URL);
                } else {
                    System.out.println(" Iniciando o frontend (npm run dev)...");
                    iniciarVite();

                    // --- AGUARDA O VITE FICAR PRONTO ---
                    boolean pronto = aguardarVite(30);

                    if (pronto) {
                        System.out.println("  Frontend iniciado com sucesso!");
                    } else {
                        System.out.println("  Frontend demorou para iniciar. Verifique o terminal.");
                    }
                }

                // --- EXIBE OS LINKS ---
                System.out.println();
                System.out.println("  +-------------------------------------------------------+");
                System.out.println("  |  Frontend (Vite):   " + FRONTEND_URL);
                System.out.println("  |  Swagger (API):     " + SWAGGER_URL);
                System.out.println("  |  Backend (API):     " + BACKEND_URL);
                System.out.println("  +-------------------------------------------------------+");
                System.out.println();

                // --- ABRE O FRONTEND NO NAVEGADOR ---
                abrirNoNavegador(FRONTEND_URL);

            } catch (Exception e) {
                System.out.println(" Nao foi possivel iniciar o frontend automaticamente.");
                System.out.println(" Inicie manualmente: cd frontend && npm run dev");
                System.out.println(" Erro: " + e.getMessage());
            }
        }, "frontend-starter").start();
    }

    // --- VERIFICA SE O VITE JA ESTA RODANDO NA PORTA 5173 ---
    private boolean isViteRodando() {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(FRONTEND_URL).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(2000);
            connection.setReadTimeout(2000);

            int responseCode = connection.getResponseCode();
            connection.disconnect();

            return responseCode == 200;
        } catch (Exception e) {
            return false;
        }
    }

    // --- INICIA O PROCESSO DO VITE ---
    private void iniciarVite() throws Exception {
        File frontendDir = new File(FRONTEND_DIR).getCanonicalFile();

        if (!frontendDir.exists()) {
            frontendDir = new File("frontend").getCanonicalFile();
        }

        if (!frontendDir.exists() || !new File(frontendDir, "package.json").exists()) {
            throw new RuntimeException("Pasta do frontend nao encontrada em: " + frontendDir.getAbsolutePath());
        }

        System.out.println(" Frontend encontrado em: " + frontendDir.getAbsolutePath());

        // --- MONTA O COMANDO PARA WINDOWS ---
        ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", "npm run dev");
        processBuilder.directory(frontendDir);
        processBuilder.redirectErrorStream(true);

        processBuilder.environment().putAll(System.getenv());

        Process process = processBuilder.start();

        // --- LE O OUTPUT DO VITE EM BACKGROUND PARA NAO TRAVAR ---
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("  [Vite] " + line);
                }
            } catch (Exception e) {
                // --- PROCESSO ENCERRADO ---
            }
        }, "vite-output-reader").start();

        // --- O VITE SERA ENCERRADO QUANDO O SPRING PARAR ---
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (process.isAlive()) {
                System.out.println("  Encerrando o frontend...");
                process.descendants().forEach(ProcessHandle::destroy);
                process.destroy();
            }
        }, "vite-shutdown-hook"));
    }

    // --- AGUARDA O VITE FICAR PRONTO ---
    private boolean aguardarVite(int timeoutSegundos) {
        int tentativas = timeoutSegundos / 2;

        for (int i = 0; i < tentativas; i++) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }

            if (isViteRodando()) {
                return true;
            }
        }
        return false;
    }

    // --- ABRE UMA URL NO NAVEGADOR PADRAO ---
    private void abrirNoNavegador(String url) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
                System.out.println(" Navegador aberto em: " + url);
            }
        } catch (Exception e) {
            System.out.println(" Nao foi possivel abrir o navegador: " + e.getMessage());
        }
    }
}

