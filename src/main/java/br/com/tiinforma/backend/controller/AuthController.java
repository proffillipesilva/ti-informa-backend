package br.com.tiinforma.backend.controller;

import br.com.tiinforma.backend.domain.criador.Criador;
import br.com.tiinforma.backend.domain.criador.CriadorCreateDto;
import br.com.tiinforma.backend.domain.dtosComuns.login.AuthLoginDto;
import br.com.tiinforma.backend.domain.dtosComuns.login.LoginResponseDto;
import br.com.tiinforma.backend.domain.enums.Funcao;
import br.com.tiinforma.backend.domain.userDetails.UserDetailsImpl;
import br.com.tiinforma.backend.domain.usuario.Usuario;
import br.com.tiinforma.backend.domain.usuario.UsuarioCreateDto;
import br.com.tiinforma.backend.repositories.CriadorRepository;
import br.com.tiinforma.backend.repositories.UsuarioRepository;
import br.com.tiinforma.backend.security.OAuth2AuthenticationSuccessHandler;
import br.com.tiinforma.backend.security.jwt.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CriadorRepository criadorRepository;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthLoginDto loginRequest) {
        var usernameSenha = new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(), loginRequest.getSenha()
        );



        var auth = this.authenticationManager.authenticate(usernameSenha);
        var principal = auth.getPrincipal();

        if (principal instanceof UserDetailsImpl userDetails) {
            String token = tokenService.gerarToken(userDetails); // Agora está correto

            return ResponseEntity.ok(new LoginResponseDto(token));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Erro ao autenticar usuário.");
    }




    @PostMapping("/register/usuario")
    public ResponseEntity registerUsuario(@RequestBody @Valid UsuarioCreateDto usuarioCreateDto) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(usuarioCreateDto.getEmail());

        if (usuarioExistente.isPresent()) {
            return ResponseEntity.badRequest().body("E-mail já cadastrado!");
        }

        if (usuarioCreateDto.getSenha() == null || usuarioCreateDto.getSenha().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Senha não pode estar vazia.");
        }

        String senhaEncriptada = new BCryptPasswordEncoder().encode(usuarioCreateDto.getSenha());

        Usuario usuario = Usuario.builder()
                .nome(usuarioCreateDto.getNome())
                .email(usuarioCreateDto.getEmail())
                .senha(senhaEncriptada)
                .funcao(Funcao.USUARIO)
                .interesses(usuarioCreateDto.getInteresses())
                .build();

        usuarioRepository.save(usuario);

        return ResponseEntity.ok("Usuário cadastrado com sucesso!");
    }




    @PostMapping("register/criador")
    public ResponseEntity registerCriador(@RequestBody @Valid CriadorCreateDto criadorCreateDto) {
        Optional<Criador> criadorExistente = criadorRepository.findByEmail(criadorCreateDto.getEmail());

        if (criadorExistente.isPresent()) {
            return ResponseEntity.badRequest().body("Email já cadastrado");
        }

        if (criadorCreateDto.getSenha() == null || criadorCreateDto.getSenha().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Senha não pode estar vazia");
        }

        String senhaEncriptada = new BCryptPasswordEncoder().encode(criadorCreateDto.getSenha());
        Criador criador = Criador.builder()
                .nome(criadorCreateDto.getNome())
                .email(criadorCreateDto.getEmail())
                .senha(senhaEncriptada)
                .funcao(Funcao.CRIADOR)
                .build();

        criadorRepository.save(criador);

        return ResponseEntity.ok("Criador cadastrado com sucesso!");
    }

    @PutMapping("/completar-cadastro/usuario")
    public ResponseEntity<?> completarCadastroUsuario(
            @RequestBody @Valid UsuarioCreateDto usuarioDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(userDetails.getId());

        if (usuarioOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
        }

        Usuario usuario = usuarioOptional.get();

        usuario.setSenha(new BCryptPasswordEncoder().encode(usuarioDto.getSenha()));
        usuario.setInteresses(usuarioDto.getInteresses());

        usuarioRepository.save(usuario);

        return ResponseEntity.ok("Cadastro atualizado com sucesso!");
    }



    @PutMapping("/completar-cadastro/criador")
    public ResponseEntity<?> completarCadastroCriador(
            @RequestBody @Valid CriadorCreateDto criadorDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Optional<Criador> criadorOptional = criadorRepository.findById(userDetails.getId());

        if (criadorOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Criador não encontrado");
        }

        Criador criador = criadorOptional.get();

        criador.setSenha(new BCryptPasswordEncoder().encode(criadorDto.getSenha()));
        criador.setCpf(criadorDto.getCpf());
        criador.setRg(criadorDto.getRg());
        criador.setFormacao(criadorDto.getFormacao());

        criadorRepository.save(criador);

        return ResponseEntity.ok("Cadastro de criador atualizado com sucesso!");
    }

}
