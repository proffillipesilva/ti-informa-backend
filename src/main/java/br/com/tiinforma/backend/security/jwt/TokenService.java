package br.com.tiinforma.backend.security.jwt;

import br.com.tiinforma.backend.domain.userDetails.UserDetailsImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    public String gerarToken(UserDetailsImpl userDetails) {
        SecretKey key = getChaveSecreta();

        List<Map<String, String>> roles = userDetails.getAuthorities().stream()
                .map(authority -> Map.of("authority", authority.getAuthority()))
                .toList();

        return Jwts.builder()
                .setIssuer("auth-api")
                .setSubject(userDetails.getEmail())
                .claim("roles", roles)  // "roles" é mais comum, mas pode manter "role" se preferir
                .setExpiration(gerarDataExpiracao())
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private Date gerarDataExpiracao() {
        return Date.from(
                LocalDateTime.now(ZoneOffset.UTC)
                        .plusHours(2)
                        .toInstant(ZoneOffset.UTC)
        );
    }

    public String extrairUsuario(String token) {
        try {
            return extrairClaims(token).getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isTokenExpirado(String token) {
        try {
            return extrairClaims(token).getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    public String validarToken(String token) {
        var usuario = extrairUsuario(token);
        if (usuario != null && !isTokenExpirado(token)) {
            return "Token válido para o usuário: " + usuario;
        }
        return "Token inválido";
    }

    private Claims extrairClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getChaveSecreta())
                .setAllowedClockSkewSeconds(60) // 1 minuto de tolerância
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private SecretKey getChaveSecreta() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}
