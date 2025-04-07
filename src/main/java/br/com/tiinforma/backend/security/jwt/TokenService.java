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

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    public String gerarToken(UserDetailsImpl userDetails) {
        SecretKey key = getChaveSecreta();
        return Jwts.builder()
                .setIssuer("auth-api")
                .setSubject(userDetails.getEmail())
                .claim("role", userDetails.getAuthorities())
                .setExpiration(gerarDataExpiracao())
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    private Date gerarDataExpiracao() {
        return Date.from(LocalDateTime.now(ZoneOffset.UTC)
                .plusHours(2)
                .toInstant(ZoneOffset.UTC));
    }


    public String extrairUsuario(String token) {
        try {
            String usuario = extrairClaims(token).getSubject();
            return usuario;
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
        String usuario = extrairUsuario(token);
        return (usuario != null && !isTokenExpirado(token))
                ? "Token válido para o usuário: " + usuario
                : "Token inválido";
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
