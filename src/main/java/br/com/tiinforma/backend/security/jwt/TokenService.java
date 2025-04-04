package br.com.tiinforma.backend.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Date;

@Service
public class TokenService {

    @Value("${jwt.secret}")
    private String secret;

    private Key getChaveSecreta() {
        byte[] decodedKey = Base64.getDecoder().decode(secret); // Decode Base64
        return Keys.hmacShaKeyFor(decodedKey);
    }
    
    public String gerarToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(gerarDataExpiracao())
                .signWith(getChaveSecreta()) // Simplified
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
                .setSigningKey(getChaveSecreta()) // Ajustado para usar o método correto
                .build()
                .parseClaimsJws(token)
                .getBody();
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
