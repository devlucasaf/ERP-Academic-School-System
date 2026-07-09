package erp.academico.infra.security;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class GeradorSenhaTemporaria {

    private static final String MAIUSCULAS = "ABCDEFGHJKLMNPQRSTUVWXYZ";
    private static final String MINUSCULAS = "abcdefghijkmnpqrstuvwxyz";
    private static final String NUMEROS = "23456789";
    private static final String ESPECIAIS = "!@#$%*";
    private static final String TODOS = MAIUSCULAS + MINUSCULAS + NUMEROS + ESPECIAIS;

    private static final int TAMANHO = 10;

    private final SecureRandom random = new SecureRandom();

    // --- GERA UMA SENHA ALEATÓRIA COM AO MENOS UM CARACTERE DE CADA TIPO ---
    public String gerar() {
        StringBuilder senha = new StringBuilder(TAMANHO);
        senha.append(sortear(MAIUSCULAS));
        senha.append(sortear(MINUSCULAS));
        senha.append(sortear(NUMEROS));
        senha.append(sortear(ESPECIAIS));

        for (int i = senha.length(); i < TAMANHO; i++) {
            senha.append(sortear(TODOS));
        }

        return embaralhar(senha.toString());
    }

    // --- SORTEIA UM CARACTERE DE UM CONJUNTO ---
    private char sortear(String conjunto) {
        return conjunto.charAt(random.nextInt(conjunto.length()));
    }

    // --- EMBARALHA OS CARACTERES PARA NÃO FIXAR A ORDEM DOS TIPOS ---
    private String embaralhar(String texto) {
        char[] caracteres = texto.toCharArray();
        for (int i = caracteres.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char tmp = caracteres[i];
            caracteres[i] = caracteres[j];
            caracteres[j] = tmp;
        }
        return new String(caracteres);
    }
}

