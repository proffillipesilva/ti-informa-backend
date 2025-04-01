package br.com.tiinforma.backend.controller;

import br.com.tiinforma.backend.domain.usuario.UsuarioLoginDto;
import br.com.tiinforma.backend.security.jwt.JwtUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private JwtUtil jwtUtil;

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/auth/login")
    public String login(@RequestBody UsuarioLoginDto loginRequest) {
        // Validar as credenciais (isso depende da sua lógica de autenticação)
        String username = loginRequest.getEmail(); // exemplo de login, substitua pela validação real

        // Gerar e retornar o token JWT
        return jwtUtil.gerarToken(username);
    }
}
