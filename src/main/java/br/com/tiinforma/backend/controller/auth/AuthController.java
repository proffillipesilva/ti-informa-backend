package br.com.tiinforma.backend.controller.auth;

import br.com.tiinforma.backend.domain.criador.Criador;
import br.com.tiinforma.backend.domain.criador.CriadorCreateDto;
import br.com.tiinforma.backend.domain.dtosComuns.login.AuthLoginDto;
import br.com.tiinforma.backend.domain.dtosComuns.login.LoginResponseDto;
import br.com.tiinforma.backend.domain.embeddedPk.PlaylistVideoId;
import br.com.tiinforma.backend.domain.enums.Funcao;
import br.com.tiinforma.backend.domain.enums.Visibilidade;
import br.com.tiinforma.backend.domain.playlist.Playlist;
import br.com.tiinforma.backend.domain.playlist.PlaylistCreateDto;
import br.com.tiinforma.backend.domain.playlist.PlaylistResponseDto;
import br.com.tiinforma.backend.domain.playlistVideo.PlaylistVideo;
import br.com.tiinforma.backend.domain.playlistVideo.PlaylistVideoResponseDto;
import br.com.tiinforma.backend.domain.userDetails.UserDetailsImpl;
import br.com.tiinforma.backend.domain.usuario.Usuario;
import br.com.tiinforma.backend.domain.usuario.UsuarioCreateDto;
import br.com.tiinforma.backend.domain.usuario.UsuarioResponseDto;
import br.com.tiinforma.backend.domain.video.Video;
import br.com.tiinforma.backend.repositories.*;
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

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CriadorRepository criadorRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private StorageService storageService;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private PlaylistVideoRepository playlistVideoRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthLoginDto loginRequest) {
        var usernameSenha = new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(), loginRequest.getSenha()
        );

        var auth = this.authenticationManager.authenticate(usernameSenha);
        var principal = auth.getPrincipal();

        if (principal instanceof UserDetailsImpl userDetails) {
            String token = tokenService.gerarToken(userDetails);

            return ResponseEntity.ok(new LoginResponseDto(token));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Erro ao autenticar usuário.");
    }




    @PostMapping("/register/usuario")
    public ResponseEntity<?> registerUsuario(@RequestBody @Valid UsuarioCreateDto usuarioCreateDto) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(usuarioCreateDto.getEmail());
        if (usuarioExistente.isPresent()) {
            return ResponseEntity.badRequest().body("E-mail já cadastrado!");
        }
        if (usuarioCreateDto.getSenha() == null || usuarioCreateDto.getSenha().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Senha não pode estar vazia.");
        }
        String senhaEncriptada = new BCryptPasswordEncoder().encode(usuarioCreateDto.getSenha());
        Funcao funcaoUsuario = Funcao.USUARIO;
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
                .funcao(funcaoUsuario)
                .interesses(usuarioCreateDto.getInteresses())
                .pergunta_resposta(perguntaRespostaJson)
                .build();
        usuarioRepository.save(usuario);
        return ResponseEntity.ok("Usuário cadastrado com sucesso!");
    }




    @PostMapping("register/criador")
    public ResponseEntity<?> registerCriador(@RequestBody @Valid CriadorCreateDto criadorCreateDto) {
        if (criadorRepository.findByCpf(criadorCreateDto.getCpf()).isPresent()) {
            return ResponseEntity.badRequest().body("CPF já cadastrado.");
        }

        if (criadorCreateDto.getSenha() == null || criadorCreateDto.getSenha().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Senha não pode estar vazia.");
        }

        String senhaFornecidaCriador = criadorCreateDto.getSenha();

        Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(criadorCreateDto.getEmail());

        if (usuarioExistente.isPresent()) {
            Usuario usuario = usuarioExistente.get();

            if (!passwordEncoder.matches(senhaFornecidaCriador, usuario.getSenha())) {
                return ResponseEntity.badRequest().body("A senha fornecida para o criador deve ser a mesma do usuário já existente com este e-mail.");
            }

            if (!usuario.getFuncao().equals(Funcao.CRIADOR) && !usuario.getFuncao().equals(Funcao.ADMINISTRADOR)) {
                usuario.setFuncao(Funcao.CRIADOR);
                usuarioRepository.save(usuario);
                log.info("Usuário com email {} atualizado para a função CRIADOR.", criadorCreateDto.getEmail());
            } else if (usuario.getFuncao().equals(Funcao.CRIADOR) || usuario.getFuncao().equals(Funcao.ADMINISTRADOR)) {
                return ResponseEntity.badRequest().body("Já existe um usuário com esta função associado a este e-mail.");
            }
        } else {
            if (criadorRepository.findByEmail(criadorCreateDto.getEmail()).isPresent()) {
                return ResponseEntity.badRequest().body("Email já cadastrado como criador. Use um email diferente ou entre em contato com o suporte.");
            }
        }

        String senhaEncriptadaCriador = passwordEncoder.encode(senhaFornecidaCriador);

        Criador criador = Criador.builder()
                .nome(criadorCreateDto.getNome())
                .email(criadorCreateDto.getEmail())
                .cpf(criadorCreateDto.getCpf())
                .formacao(criadorCreateDto.getFormacao())
                .senha(senhaEncriptadaCriador)
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
        criador.setFormacao(criadorDto.getFormacao());

        criadorRepository.save(criador);

        return ResponseEntity.ok("Cadastro de criador atualizado com sucesso!");
    }


    @PutMapping("/usuario/interesses")
    public ResponseEntity<?> atualizarInteressesUsuario(
            @RequestBody Map<String, String> requestBody,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        String interesses = requestBody.get("interesses");

        Optional<Usuario> usuarioOptional = usuarioRepository.findById(userDetails.getId());
        if (usuarioOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
        }

        Usuario usuario = usuarioOptional.get();
        usuario.setInteresses(interesses);
        usuarioRepository.save(usuario);

        return ResponseEntity.ok("Interesses atualizados com sucesso!");
    }

    @GetMapping("/me")
    public ResponseEntity<?> getLoggedInUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Não autenticado");
        }

        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(userDetails.getUsername());

        if (usuarioOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
        }

        Usuario usuario = usuarioOptional.get();

        if (usuario.getFuncao().equals(Funcao.CRIADOR)) {
            Optional<Criador> criadorOptional = criadorRepository.findByEmail(usuario.getEmail());

            if (criadorOptional.isPresent()) {
                Criador criador = criadorOptional.get();
                return ResponseEntity.ok(Map.of(
                        "nome", criador.getNome(),
                        "email", criador.getEmail(),
                        "isCriador", true,
                        "funcao", criador.getFuncao().name(),
                        "formacao", criador.getFormacao() != null ? criador.getFormacao() : ""
                ));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Consistência de dados comprometida: Usuário com função CRIADOR sem registro de Criador.");
            }
        } else {
            return ResponseEntity.ok(Map.of(
                    "nome", usuario.getNome(),
                    "email", usuario.getEmail(),
                    "isCriador", false,
                    "funcao", usuario.getFuncao().name(),
                    "interesses", usuario.getInteresses() != null ? usuario.getInteresses() : ""
            ));
        }
    }

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

    @PutMapping("/recuperar-senha/redefinir")
    public ResponseEntity<?> redefinirSenha(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String novaSenha = payload.get("novaSenha");

        if (email == null || novaSenha == null || email.isBlank() || novaSenha.isBlank()) {
            return ResponseEntity.badRequest().body("Email e nova senha são obrigatórios.");
        }

        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(email);
        if (usuarioOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado.");
        }

        Usuario usuario = usuarioOptional.get();
        String senhaEncriptada = new BCryptPasswordEncoder().encode(novaSenha);
        usuario.setSenha(senhaEncriptada);
        usuarioRepository.save(usuario);

        return ResponseEntity.ok("Senha redefinida com sucesso.");
    }

}