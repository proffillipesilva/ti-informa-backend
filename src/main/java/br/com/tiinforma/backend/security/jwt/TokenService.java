package br.com.tiinforma.backend.security.jwt;

import br.com.tiinforma.backend.domain.userDetails.UserDetailsImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Date;

@Service
public class TokenService {
    @Value("${api.security.token.secret}")
    private String secret;

    public String gerarToken(UserDetailsImpl userDetails) {
        SecretKey key = getChaveSecreta();
        return Jwts.builder()
                .setIssuer("auth-api")
                .setSubject(userDetails.getUsername()) // ou getLogin(), dependendo da implementação
                .claim("role", userDetails.getAuthorities()) // Adiciona perfil (opcional)
                .setExpiration(gerarDataExpiracao())
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private Date gerarDataExpiracao() {
        return Date.from(LocalDateTime.now()
                .plusHours(2)
                .toInstant(ZoneOffset.UTC)); // UTC
    }

    public String extrairUsuario(String token) {
        try {
            return extrairClaims(token).getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    private Claims extrairClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getChaveSecreta()) // Agora o método existe e funciona corretamente
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private SecretKey getChaveSecreta() {
        byte[] decodedKey = Base64.getDecoder().decode(secret); // Decodifica corretamente a chave Base64
        return Keys.hmacShaKeyFor(decodedKey);
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

        if (usuario != null && !isTokenExpirado(token)) {
            return "Token válido para o usuário: " + usuario;
        } else {
            return "Token inválido";
        }
    }
}
