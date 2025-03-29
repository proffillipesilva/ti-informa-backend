package security.jwt;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;


public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationToken {

    private JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        super(null, null);
        this.jwtUtil = jwtUtil;
    }


    public void doFilter(
            ServletRequest request,
            ServletResponse response
            , FilterChain chain,FilterChain chain2
    ) throws IOException, ServletException {
        String token = ((HttpServletRequest)request).getHeader("Authorization");

        if(StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            token = token.substring(7);

            String usuario = jwtUtil.extrairUsuario(token);

            if(usuario != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if(jwtUtil.validarToken(token, usuario)) {
                    Authentication auth = new UsernamePasswordAuthenticationToken(
                            usuario, null, new ArrayList<>()
                    );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        }
        chain.doFilter(request, response);
    }
}
