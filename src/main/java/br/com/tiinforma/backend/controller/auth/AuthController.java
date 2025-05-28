package br.com.tiinforma.backend.controller.auth;

import br.com.tiinforma.backend.domain.dtosComuns.AtualizarSenhaDto;
import br.com.tiinforma.backend.domain.usuario.Usuario;
import br.com.tiinforma.backend.domain.usuario.UsuarioCreateDto;
import br.com.tiinforma.backend.domain.usuario.UsuarioResponseDto;
import br.com.tiinforma.backend.domain.userDetails.UserDetailsImpl;
import br.com.tiinforma.backend.repositories.UsuarioRepository;
import br.com.tiinforma.backend.repositories.CriadorRepository;
import br.com.tiinforma.backend.security.jwt.TokenService;
import br.com.tiinforma.backend.services.aws.StorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UsuarioRepository usuarioRepository;
    private final CriadorRepository criadorRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final ObjectMapper objectMapper;
    private final StorageService storageService;

    @Autowired
    public AuthController(
            UsuarioRepository usuarioRepository,
            CriadorRepository criadorRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            TokenService tokenService,
            ObjectMapper objectMapper,
            StorageService storageService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.criadorRepository = criadorRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.objectMapper = objectMapper;
        this.storageService = storageService;
    }

    // Cadastro de usuário comum
    @PostMapping("/registrar")
    public ResponseEntity<?> registrarUsuario(@RequestBody @Valid UsuarioCreateDto usuarioCreateDto) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(usuarioCreateDto.getEmail());
        if (usuarioExistente.isPresent()) {
            return ResponseEntity.badRequest().body("E-mail já cadastrado!");
        }
        if (usuarioCreateDto.getSenha() == null || usuarioCreateDto.getSenha().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Senha não pode estar vazia.");
        }
        String senhaEncriptada = passwordEncoder.encode(usuarioCreateDto.getSenha());
        String perguntaRespostaJson = null;
        if (usuarioCreateDto.getPergunta_resposta() != null && !usuarioCreateDto.getPergunta_resposta().isEmpty()) {
            try {
                perguntaRespostaJson = objectMapper.writeValueAsString(usuarioCreateDto.getPergunta_resposta().stream()
                        .collect(Collectors.toMap(UsuarioResponseDto::getPergunta, UsuarioResponseDto::getResposta)));
                if (perguntaRespostaJson.length() > 100) {
                    return ResponseEntity.badRequest().body("A combinação de perguntas e respostas excede o limite de 100 caracteres.");
                }
            } catch (Exception e) {
                return ResponseEntity.internalServerError().body("Erro ao processar as perguntas de segurança.");
            }
        }
        Usuario usuario = Usuario.builder()
                .nome(usuarioCreateDto.getNome())
                .email(usuarioCreateDto.getEmail())
                .senha(senhaEncriptada)
                .funcao(usuarioCreateDto.getFuncao())
                .interesses(usuarioCreateDto.getInteresses())
                .pergunta_resposta(perguntaRespostaJson)
                .build();
        usuarioRepository.save(usuario);
        return ResponseEntity.ok("Usuário cadastrado com sucesso!");
    }

    // Autenticação (login)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginDto) {
        String email = loginDto.get("email");
        String senha = loginDto.get("senha");

        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(email);
        if (usuarioOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário ou senha incorretos.");
        }
        Usuario usuario = usuarioOptional.get();
        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário ou senha incorretos.");
        }
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, senha));
        String token = tokenService.gerarToken(new UserDetailsImpl(usuario));
        return ResponseEntity.ok(Map.of("token", token));
    }

    // Completar cadastro do usuário autenticado
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
        usuario.setSenha(passwordEncoder.encode(usuarioDto.getSenha()));
        usuario.setInteresses(usuarioDto.getInteresses());
        usuarioRepository.save(usuario);
        return ResponseEntity.ok("Cadastro atualizado com sucesso!");
    }

    // Alterar senha do usuário autenticado
    @PutMapping("/alterar-senha")
    public ResponseEntity<?> alterarSenha(
            @RequestBody @Valid AtualizarSenhaDto atualizarSenhaDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(userDetails.getId());
        if (usuarioOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
        }
        Usuario usuario = usuarioOptional.get();
        if (!passwordEncoder.matches(atualizarSenhaDto.getSenhaAntiga(), usuario.getSenha())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Senha antiga incorreta.");
        }
        usuario.setSenha(passwordEncoder.encode(atualizarSenhaDto.getNovaSenha()));
        usuarioRepository.save(usuario);
        return ResponseEntity.ok("Senha alterada com sucesso!");
    }

    // Recuperar pergunta de segurança (para esqueceu a senha)
    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/recuperar-senha/pergunta")
    public ResponseEntity<?> buscarPerguntaSeguranca(@RequestParam String email) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(email);
        if (usuarioOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("E-mail não encontrado.");
        }
        Usuario usuario = usuarioOptional.get();
        String perguntaRespostaJson = usuario.getPergunta_resposta();
        if (perguntaRespostaJson == null || perguntaRespostaJson.isEmpty()) {
            return ResponseEntity.ok("");
        }
        try {
            Map<String, String> perguntasRespostas = objectMapper.readValue(perguntaRespostaJson, Map.class);
            if (!perguntasRespostas.isEmpty()) {
                return ResponseEntity.ok(perguntasRespostas.keySet().iterator().next());
            } else {
                return ResponseEntity.ok("");
            }
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Erro ao processar as perguntas de segurança.");
        }
    }

    // Verificar resposta da pergunta de segurança
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/recuperar-senha/verificar-resposta")
    public ResponseEntity<?> verificarRespostaSeguranca(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String resposta = payload.get("resposta");
        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(email);
        if (usuarioOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("E-mail não encontrado.");
        }
        Usuario usuario = usuarioOptional.get();
        String perguntaRespostaJson = usuario.getPergunta_resposta();
        if (perguntaRespostaJson == null || perguntaRespostaJson.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Pergunta de segurança não configurada para este usuário.");
        }
        try {
            Map<String, String> perguntasRespostas = objectMapper.readValue(perguntaRespostaJson, Map.class);
            for (Map.Entry<String, String> entry : perguntasRespostas.entrySet()) {
                if (entry.getValue().equals(resposta)) {
                    return ResponseEntity.ok().build();
                }
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Resposta incorreta.");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Erro ao processar as perguntas de segurança.");
        }
    }

    // Redefinir a senha após validação da pergunta de segurança
    @PutMapping("/recuperar-senha/redefinir")
    public ResponseEntity<?> redefinirSenhaEsquecida(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String novaSenha = payload.get("novaSenha");
        if (novaSenha == null || novaSenha.length() < 8) {
            return ResponseEntity.badRequest().body("A nova senha deve ter no mínimo 8 caracteres.");
        }
        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(email);
        if (usuarioOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("E-mail não encontrado.");
        }
        Usuario usuario = usuarioOptional.get();
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);
        return ResponseEntity.ok("Senha redefinida com sucesso!");
    }

    // (Opcional) Listar todos os usuários (exemplo administrativo)
    @GetMapping("/usuarios")
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return ResponseEntity.ok(usuarios);
    }
}