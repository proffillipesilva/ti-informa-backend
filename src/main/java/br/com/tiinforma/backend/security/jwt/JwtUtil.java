package br.com.tiinforma.backend.security.jwt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private static final SecretKey CHAVE_SECRETA = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Tempo de expiração do token em milissegundos (exemplo de 1 hora)
    private static final long DATA_DE_EXPIRACAO = 3600000;

    public String gerarToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + DATA_DE_EXPIRACAO))
                .signWith(CHAVE_SECRETA) // Usando a chave gerada de 256 bits
                .compact();
    }

    // Extrair o usuário do token
    public String extrairUsuario(String token) {
        return extrairClaims(token).getSubject();
    }

    // Extrair as claims do token
    private Claims extrairClaims(String token) {
        return Jwts.parserBuilder()  // Usando parserBuilder() em vez de parser()
                .setSigningKey(CHAVE_SECRETA)  // Definir a chave secreta
                .build()
                .parseClaimsJws(token)  // Agora, chamamos parseClaimsJws após build()
                .getBody();
    }


    // Verificar se o token está expirado
    public boolean isTokenExpirado(String token) {
        return extrairClaims(token).getExpiration().before(new Date());
    }

    // Validar o token
    public boolean validarToken(String token, String usuario) {
        return (usuario.equals(extrairUsuario(token)) && !isTokenExpirado(token));
    }
}
