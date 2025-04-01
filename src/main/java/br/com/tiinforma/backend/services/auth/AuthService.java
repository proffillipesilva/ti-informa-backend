package br.com.tiinforma.backend.services.auth;

import br.com.tiinforma.backend.domain.usuario.Usuario;
import br.com.tiinforma.backend.repositories.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UsuarioRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public boolean authenticate(String username, String password) {
        Usuario user = userRepository.findByEmail(username);
        if (user != null) {
            return passwordEncoder.matches(password, user.getPassword()); // Verifica se as senhas batem
        }
        return false;
    }

    public Usuario registrarUsuario(String nome, String password) {
        String encodedPassword = passwordEncoder.encode(password);
        Usuario user = Usuario.builder()
                .nome(nome)
                .password(encodedPassword)
                .build();
        return userRepository.save(user);
    }

}
