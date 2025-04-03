package br.com.tiinforma.backend.controller;

import br.com.tiinforma.backend.domain.criador.Criador;
import br.com.tiinforma.backend.domain.criador.CriadorCreateDto;
import br.com.tiinforma.backend.domain.dtosComuns.AuthLoginDto;
import br.com.tiinforma.backend.domain.enums.Funcao;
import br.com.tiinforma.backend.domain.usuario.Usuario;
import br.com.tiinforma.backend.domain.usuario.UsuarioCreateDto;
import br.com.tiinforma.backend.repositories.CriadorRepository;
import br.com.tiinforma.backend.repositories.UsuarioRepository;
import br.com.tiinforma.backend.security.jwt.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CriadorRepository criadorRepository;

    private JwtUtil jwtUtil;

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AuthLoginDto loginRequest) {
        var usernameSenha = new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(), loginRequest.getSenha()
        );

        var auth = this.authenticationManager.authenticate(usernameSenha);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/register/usuario")
    public ResponseEntity registerUsuario (@RequestBody @Valid UsuarioCreateDto usuarioCreateDto){
        if (this.usuarioRepository.findByEmail(usuarioCreateDto.getEmail()) != null){
            return ResponseEntity.badRequest().build();
        }
        String senhaEncriptada = new BCryptPasswordEncoder().encode(usuarioCreateDto.getSenha());
        Usuario usuario = Usuario.builder()
                .nome(usuarioCreateDto.getNome())
                .email(usuarioCreateDto.getEmail())
                .senha(senhaEncriptada)
                .funcao(Funcao.USUARIO) // Definindo a função como USUARIO
                .build();

        usuarioRepository.save(usuario);

        return ResponseEntity.ok().build();
    }

    @PostMapping("register/criador")
    public ResponseEntity registerCriador(@RequestBody @Valid CriadorCreateDto criadorCreateDto) {
        if (this.criadorRepository.findByEmail(criadorCreateDto.getEmail()) != null) {
            return ResponseEntity.badRequest().build();
        }

        String senhaEncriptada = new BCryptPasswordEncoder().encode(criadorCreateDto.getSenha());
        Criador criador = Criador.builder()
                .nome(criadorCreateDto.getNome())
                .email(criadorCreateDto.getEmail())
                .senha(senhaEncriptada)
                .funcao(Funcao.CRIADOR)
                .build();

        criadorRepository.save(criador);

        return ResponseEntity.ok().build();
    }
}
