package br.com.tiinforma.backend.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;


import java.io.IOException;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, jakarta.servlet.ServletException {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();

        System.out.println("Login bem-sucedido com o Google!");
        System.out.println("Detalhes do usuário: " + oauth2User.getAttributes());

        response.sendRedirect("/home");
    }
}
