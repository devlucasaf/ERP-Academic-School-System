package erp.academico.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import erp.academico.exception.BusinessException;
import erp.academico.modules.usuario.model.Usuario;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    // --- NOMES DAS CLAIMS ARMAZENADAS NO TOKEN ---
    private static final String CLAIM_PERFIL = "role";
    private static final String CLAIM_ID_USUARIO = "uid";
    private static final String CLAIM_TIPO = "type";

    // --- VALORES POSSIVEIS PARA A CLAIM DE TIPO ---
    private static final String TIPO_ACESSO = "access";
    private static final String TIPO_RENOVACAO = "refresh";

    @Value("${security.jwt.secret}")
    private String segredo;

    @Value("${security.jwt.issuer}")
    private String emissor;

    @Value("${security.jwt.expiration-minutes}")
    private long minutosExpiracao;

    @Value("${security.jwt.refresh-expiration-minutes}")
    private long minutosExpiracaoRenovacao;

    // --- GERA O ACCESS TOKEN ---
    public String gerarToken(Usuario usuario) {
        return JWT.create()
                .withIssuer(emissor)
                .withSubject(usuario.getEmail())
                .withClaim(CLAIM_ID_USUARIO, usuario.getId().toString())
                .withClaim(CLAIM_PERFIL, usuario.getRole().name())
                .withClaim(CLAIM_TIPO, TIPO_ACESSO)
                .withIssuedAt(Instant.now())
                .withExpiresAt(expiracao(minutosExpiracao))
                .sign(algoritmo());
    }

    // --- GERA O REFRESH TOKEN USADO PARA RENOVAR O ACCESS TOKEN ---
    public String gerarRefreshToken(Usuario usuario) {
        return JWT.create()
                .withIssuer(emissor)
                .withSubject(usuario.getEmail())
                .withClaim(CLAIM_ID_USUARIO, usuario.getId().toString())
                .withClaim(CLAIM_TIPO, TIPO_RENOVACAO)
                .withIssuedAt(Instant.now())
                .withExpiresAt(expiracao(minutosExpiracaoRenovacao))
                .sign(algoritmo());
    }

    // --- VALIDA UM ACCESS TOKEN E RETORNA O SUBJECT ---
    public String validarToken(String token) {
        DecodedJWT decodificado = verificar(token);
        String tipo = decodificado.getClaim(CLAIM_TIPO).asString();
        if (tipo != null && !TIPO_ACESSO.equals(tipo)) {
            throw new BusinessException("Token informado não é um access token.");
        }
        return decodificado.getSubject();
    }

    // --- VALIDA UM REFRESH TOKEN E RETORNA O SUBJECT ---
    public String validarRefreshToken(String token) {
        DecodedJWT decodificado = verificar(token);
        String tipo = decodificado.getClaim(CLAIM_TIPO).asString();
        if (!TIPO_RENOVACAO.equals(tipo)) {
            throw new BusinessException("Token informado não é um refresh token.");
        }
        return decodificado.getSubject();
    }

    // --- VERIFICA ASSINATURA E ISSUER DO TOKEN ---
    private DecodedJWT verificar(String token) {
        try {
            JWTVerifier verificador = JWT.require(algoritmo())
                    .withIssuer(emissor)
                    .build();
            return verificador.verify(token);
        } catch (JWTVerificationException ex) {
            throw new BusinessException("Token JWT inválido ou expirado.");
        }
    }

    // --- HMAC256 USANDO O SECRET CONFIGURADO ---
    private Algorithm algoritmo() {
        return Algorithm.HMAC256(segredo);
    }

    // --- CALCULA O Instant DE EXPIRAÇÃO A PARTIR DE MINUTOS ---
    private Instant expiracao(long minutos) {
        return LocalDateTime.now()
                .plusMinutes(minutos)
                .toInstant(ZoneOffset.of("-03:00"));
    }
}
