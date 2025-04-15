package br.com.tiinforma.backend.security;

import br.com.tiinforma.backend.domain.criador.Criador;
import br.com.tiinforma.backend.domain.enums.Funcao;
import br.com.tiinforma.backend.domain.userDetails.UserDetailsImpl;
import br.com.tiinforma.backend.domain.usuario.Usuario;
import br.com.tiinforma.backend.repositories.CriadorRepository;
import br.com.tiinforma.backend.repositories.UsuarioRepository;
import br.com.tiinforma.backend.security.jwt.TokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.util.Optional;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CriadorRepository criadorRepository;

    @Autowired
    private TokenService tokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");
        String nome = oauth2User.getAttribute("name");

        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(email);
        Optional<Criador> criadorOptional = criadorRepository.findByEmail(email);

        String token;

        if (usuarioOptional.isPresent()) {
            // Já é um usuário comum
            Usuario usuario = usuarioOptional.get();
            UserDetailsImpl userDetails = new UserDetailsImpl(usuario);
            token = tokenService.gerarToken(userDetails);
        } else if (criadorOptional.isPresent()) {
            // Já é um criador
            Criador criador = criadorOptional.get();
            UserDetailsImpl userDetails = new UserDetailsImpl(criador);
            token = tokenService.gerarToken(userDetails);
        } else {
            // Novo usuário Google - você pode decidir se cadastra como USUARIO ou CRIADOR
            Usuario novoUsuario = Usuario.builder()
                    .nome(nome)
                    .email(email)
                    .senha("")
                    .funcao(Funcao.USUARIO)
                    .build();
            usuarioRepository.save(novoUsuario);

            UserDetailsImpl userDetails = new UserDetailsImpl(novoUsuario);
            token = tokenService.gerarToken(userDetails);
        }

        // Redirecionar para o frontend com o token JWT
        String redirectUrl = "http://localhost:3000/oauth2/success?token=" + token; // adapte sua URL
        response.sendRedirect(redirectUrl);
    }
}

