package br.com.tiinforma.backend.controller.auth;

import br.com.tiinforma.backend.domain.criador.Criador;
import br.com.tiinforma.backend.domain.criador.CriadorCreateDto;
import br.com.tiinforma.backend.domain.dtosComuns.login.AuthLoginDto;
import br.com.tiinforma.backend.domain.dtosComuns.login.GoogleLoginRequestDto;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
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
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
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
                .cadastroCompleto(true)
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

        Map<String, Object> response = new HashMap<>();
        response.put("id", usuario.getId());
        response.put("nome", usuario.getNome());
        response.put("email", usuario.getEmail());
        response.put("funcao", usuario.getFuncao().name());
        response.put("isAdmin", usuario.getFuncao().equals(Funcao.ADMINISTRADOR));
        response.put("isCriador", usuario.getFuncao().equals(Funcao.CRIADOR));

        if (usuario.getFuncao().equals(Funcao.CRIADOR)) {
            Optional<Criador> criadorOptional = criadorRepository.findByEmail(usuario.getEmail());
            if (criadorOptional.isPresent()) {
                Criador criador = criadorOptional.get();
                response.put("formacao", criador.getFormacao() != null ? criador.getFormacao() : "");
            }
        } else {
            response.put("interesses", usuario.getInteresses() != null ? usuario.getInteresses() : "");
            response.put("fotoUrl", usuario.getFotoUrl() != null ? usuario.getFotoUrl() : "");
        }

        return ResponseEntity.ok(response);
    }

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

    @PostMapping("/google-auth")
    public ResponseEntity<?> googleAuth(@RequestBody GoogleLoginRequestDto googleLoginRequest) {
        try {
            log.info("Recebida requisição para /google-auth com idToken");
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(googleLoginRequest.getIdToken());

            String email = decodedToken.getEmail();
            String name = decodedToken.getName() != null ? decodedToken.getName() : "Usuário Google";

            if (email == null || email.isEmpty()) {
                log.error("Email nulo ou vazio obtido do token do Google");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Não foi possível obter o email do token do Google.");
            }

            Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(email);
            Usuario usuario;

            if (usuarioExistente.isPresent()) {
                usuario = usuarioExistente.get();
                log.info("Usuário existente encontrado: {}", usuario.getEmail());

                boolean cadastroCompleto = usuario.isCadastroCompleto() &&
                        usuario.getPergunta_resposta() != null &&
                        !usuario.getPergunta_resposta().isEmpty() &&
                        usuario.getInteresses() != null &&
                        !usuario.getInteresses().isEmpty();

                if (!cadastroCompleto) {
                    log.info("Cadastro incompleto para usuário: {}", email);
                    usuario.setCadastroCompleto(false);
                    usuarioRepository.save(usuario);
                }

                String token = tokenService.gerarToken(new UserDetailsImpl(usuario));
                return ResponseEntity.ok(new LoginResponseDto(
                        token,
                        usuario.getFuncao().name(),
                        cadastroCompleto
                ));

            } else {
                log.info("Criando novo usuário para: {}", email);
                String generatedPassword = passwordEncoder.encode(UUID.randomUUID().toString());

                Usuario novoUsuario = Usuario.builder()
                        .nome(name)
                        .email(email)
                        .senha(generatedPassword)
                        .funcao(Funcao.USUARIO)
                        .cadastroCompleto(false)
                        .build();

                usuario = usuarioRepository.save(novoUsuario);

                String token = tokenService.gerarToken(new UserDetailsImpl(usuario));
                return ResponseEntity.ok(new LoginResponseDto(
                        token,
                        usuario.getFuncao().name(),
                        false
                ));
            }

        } catch (FirebaseAuthException e) {
            log.error("Erro de autenticação Firebase: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token do Google inválido ou expirado.");
        } catch (Exception e) {
            log.error("Erro inesperado: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno ao processar login com Google.");
        }
    }

    @PostMapping("/register/google")
    public ResponseEntity<?> registerGoogleUser(
            @RequestBody GoogleLoginRequestDto googleRequest,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(googleRequest.getIdToken());
            String email = decodedToken.getEmail();
            String nome = decodedToken.getName();

            if (userDetails != null && !email.equals(userDetails.getUsername())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email não corresponde ao usuário autenticado.");
            }

            Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(email);
            Usuario usuario;

            if (usuarioOptional.isEmpty()) {
                String generatedPassword = passwordEncoder.encode(UUID.randomUUID().toString());

                usuario = Usuario.builder()
                        .nome(nome)
                        .email(email)
                        .senha(generatedPassword)
                        .interesses(googleRequest.getInteresses())
                        .funcao(Funcao.USUARIO)
                        .cadastroCompleto(false)
                        .build();
                usuarioRepository.save(usuario);

                String token = tokenService.gerarToken(new UserDetailsImpl(usuario));
                return ResponseEntity.status(HttpStatus.CREATED).body(new LoginResponseDto(token, usuario.getFuncao().name(), usuario.isCadastroCompleto()));

            } else {
                usuario = usuarioOptional.get();

                String perguntaRespostaJson = null;
                if (googleRequest.getPergunta_resposta() != null && !googleRequest.getPergunta_resposta().isEmpty()) {
                    try {
                        perguntaRespostaJson = objectMapper.writeValueAsString(
                                googleRequest.getPergunta_resposta().stream()
                                        .collect(Collectors.toMap(
                                                UsuarioResponseDto::getPergunta,
                                                UsuarioResponseDto::getResposta
                                        ))
                        );
                    } catch (Exception e) {
                        return ResponseEntity.internalServerError().body("Erro ao processar as perguntas de segurança.");
                    }
                }

                usuario.setInteresses(googleRequest.getInteresses());
                usuario.setPergunta_resposta(perguntaRespostaJson);
                if (googleRequest.getInteresses() != null && !googleRequest.getInteresses().isEmpty() &&
                        googleRequest.getPergunta_resposta() != null && !googleRequest.getPergunta_resposta().isEmpty()) {
                    usuario.setCadastroCompleto(true);
                } else {
                    usuario.setCadastroCompleto(false);
                }

                usuarioRepository.save(usuario);

                String token = tokenService.gerarToken(new UserDetailsImpl(usuario));

                return ResponseEntity.ok(new LoginResponseDto(token, usuario.getFuncao().name(), usuario.isCadastroCompleto()));
            }

        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token do Google inválido: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao processar cadastro: " + e.getMessage());
        }
    }

}