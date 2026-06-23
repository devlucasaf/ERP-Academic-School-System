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

    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_USER_ID = "uid";
    private static final String CLAIM_TYPE = "type";
    private static final String TYPE_ACCESS = "access";
    private static final String TYPE_REFRESH = "refresh";

    @Value("${security.jwt.secret}")
    private String secret;

    @Value("${security.jwt.issuer}")
    private String issuer;

    @Value("${security.jwt.expiration-minutes}")
    private long expirationMinutes;

    @Value("${security.jwt.refresh-expiration-minutes}")
    private long refreshExpirationMinutes;

    public String gerarToken(Usuario usuario) {
        return JWT.create()
                .withIssuer(issuer)
                .withSubject(usuario.getEmail())
                .withClaim(CLAIM_USER_ID, usuario.getId().toString())
                .withClaim(CLAIM_ROLE, usuario.getRole().name())
                .withClaim(CLAIM_TYPE, TYPE_ACCESS)
                .withIssuedAt(Instant.now())
                .withExpiresAt(expiration(expirationMinutes))
                .sign(algorithm());
    }

    public String gerarRefreshToken(Usuario usuario) {
        return JWT.create()
                .withIssuer(issuer)
                .withSubject(usuario.getEmail())
                .withClaim(CLAIM_USER_ID, usuario.getId().toString())
                .withClaim(CLAIM_TYPE, TYPE_REFRESH)
                .withIssuedAt(Instant.now())
                .withExpiresAt(expiration(refreshExpirationMinutes))
                .sign(algorithm());
    }

    /**
     * Valida um access token e retorna o subject (email).
     */
    public String validarToken(String token) {
        DecodedJWT decoded = verify(token);
        String type = decoded.getClaim(CLAIM_TYPE).asString();
        if (type != null && !TYPE_ACCESS.equals(type)) {
            throw new BusinessException("Token informado não é um access token.");
        }
        return decoded.getSubject();
    }

    /**
     * Valida um refresh token e retorna o subject (email).
     */
    public String validarRefreshToken(String token) {
        DecodedJWT decoded = verify(token);
        String type = decoded.getClaim(CLAIM_TYPE).asString();
        if (!TYPE_REFRESH.equals(type)) {
            throw new BusinessException("Token informado não é um refresh token.");
        }
        return decoded.getSubject();
    }

    private DecodedJWT verify(String token) {
        try {
            JWTVerifier verifier = JWT.require(algorithm())
                    .withIssuer(issuer)
                    .build();
            return verifier.verify(token);
        } catch (JWTVerificationException ex) {
            throw new BusinessException("Token JWT inválido ou expirado.");
        }
    }

    private Algorithm algorithm() {
        return Algorithm.HMAC256(secret);
    }

    private Instant expiration(long minutes) {
        return LocalDateTime.now()
                .plusMinutes(minutes)
                .toInstant(ZoneOffset.of("-03:00"));
    }
}

