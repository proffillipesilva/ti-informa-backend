package br.com.tiinforma.backend.controller.auth;

import br.com.tiinforma.backend.domain.PerguntaResposta.PerguntaRespostaDto;
import br.com.tiinforma.backend.domain.criador.Criador;
import br.com.tiinforma.backend.domain.criador.CriadorCreateDto;
import br.com.tiinforma.backend.domain.dtosComuns.login.AuthLoginDto;
import br.com.tiinforma.backend.domain.dtosComuns.login.LoginResponseDto;
import br.com.tiinforma.backend.domain.embeddedPk.PlaylistVideoId;
import br.com.tiinforma.backend.domain.enums.Funcao;
import br.com.tiinforma.backend.domain.enums.Visibilidade;
import br.com.tiinforma.backend.domain.playlist.Playlist;
import br.com.tiinforma.backend.domain.playlist.PlaylistDto;
import br.com.tiinforma.backend.domain.playlistVideo.PlaylistVideo;
import br.com.tiinforma.backend.domain.playlistVideo.PlaylistVideoDto;
import br.com.tiinforma.backend.domain.userDetails.UserDetailsImpl;
import br.com.tiinforma.backend.domain.usuario.Usuario;
import br.com.tiinforma.backend.domain.usuario.UsuarioCreateDto;
import br.com.tiinforma.backend.domain.video.Video;
import br.com.tiinforma.backend.repositories.*;
import br.com.tiinforma.backend.security.jwt.TokenService;
import br.com.tiinforma.backend.services.aws.StorageService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
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
                        .collect(Collectors.toMap(PerguntaRespostaDto::getPergunta, PerguntaRespostaDto::getResposta)));
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

    @PostMapping("/register/criador")
    public ResponseEntity<?> registerCriador(@RequestBody @Valid CriadorCreateDto criadorCreateDto) {

        Optional<Criador> criadorExistenteEmail = criadorRepository.findByEmail(criadorCreateDto.getEmail());
        if (criadorExistenteEmail.isPresent()) {
            return ResponseEntity.badRequest().body("Email já cadastrado");
        }

        Optional<Criador> criadorExistenteCpf = criadorRepository.findByCpf(criadorCreateDto.getCpf());
        if (criadorExistenteCpf.isPresent()) {
            return ResponseEntity.badRequest().body("CPF já cadastrado");
        }

        if (criadorCreateDto.getSenha() == null || criadorCreateDto.getSenha().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Senha não pode estar vazia");
        }

        String senhaEncriptada = new BCryptPasswordEncoder().encode(criadorCreateDto.getSenha());

        Criador criador = Criador.builder()
                .nome(criadorCreateDto.getNome())
                .email(criadorCreateDto.getEmail())
                .senha(senhaEncriptada)
                .cpf(criadorCreateDto.getCpf())
                .formacao(criadorCreateDto.getFormacao())
                .funcao(Funcao.CRIADOR)
                .build();

        criadorRepository.save(criador);

        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(criadorCreateDto.getEmail());
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            usuario.setFuncao(Funcao.CRIADOR);
            usuarioRepository.save(usuario);
        }

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

    @GetMapping("/me")
    public ResponseEntity<?> getLoggedInUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails != null) {
            Criador criador = criadorRepository.findByEmail(userDetails.getUsername()).orElse(null);
            if (criador != null) {
                return ResponseEntity.ok(Map.of(
                        "nome", criador.getNome(),
                        "email", criador.getEmail(),
                        "isCriador", true,
                        "funcao", criador.getFuncao().name()
                ));
            } else {
                Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername()).orElse(null);
                if (usuario != null) {
                    return ResponseEntity.ok(Map.of(
                            "nome", usuario.getNome(),
                            "email", usuario.getEmail(),
                            "isCriador", false,
                            "funcao", usuario.getFuncao().name()
                    ));
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
                }
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Não autenticado");
    }

    @PostMapping("/criar-playlist")
    public ResponseEntity<?> criarPlaylist(
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        String nome = body.get("nome");
        String visibilidadeStr = body.get("visibilidade");
        if (nome == null || visibilidadeStr == null) {
            return ResponseEntity.badRequest().body("Nome e visibilidade são obrigatórios.");
        }
        Visibilidade visibilidade = Visibilidade.valueOf(visibilidadeStr);
        Usuario usuario = usuarioRepository.findById(userDetails.getId()).orElse(null);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não encontrado.");
        }
        Playlist playlist = Playlist.builder()
                .nome(nome)
                .visibilidade(visibilidade)
                .usuario(usuario)
                .build();
        playlistRepository.save(playlist);
        PlaylistDto dto = new PlaylistDto(
                playlist.getId_playlist(),
                usuario.getId_usuario(),
                playlist.getNome(),
                playlist.getVisibilidade(),
                List.of()
        );
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/minhas-playlists")
    public ResponseEntity<?> listarMinhasPlaylists(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Usuario usuario = usuarioRepository.findById(userDetails.getId()).orElse(null);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não encontrado.");
        }
        List<Playlist> playlists = playlistRepository.findByUsuario(usuario);
        List<PlaylistDto> dtos = playlists.stream().map(playlist -> new PlaylistDto(
                playlist.getId_playlist(),
                usuario.getId_usuario(),
                playlist.getNome(),
                playlist.getVisibilidade(),
                playlist.getPlaylistVideos().stream().map(pv -> new PlaylistVideoDto(
                        pv.getVideo().getId_video(),
                        pv.getVideo().getTitulo(),
                        pv.getVideo().getKey(),
                        pv.getPosicaoVideo()
                )).toList()
        )).toList();
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/playlist/{playlistId}/adicionar-video")
    public ResponseEntity<?> adicionarVideoAPlaylist(
            @PathVariable Long playlistId,
            @RequestParam Long videoId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Optional<Playlist> playlistOpt = playlistRepository.findById(playlistId);
        if (playlistOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Playlist não encontrada");
        }
        Playlist playlist = playlistOpt.get();

        if (!playlist.getUsuario().getId_usuario().equals(userDetails.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Você não tem permissão para alterar esta playlist");
        }

        Optional<Video> videoOpt = videoRepository.findById(videoId);
        if (videoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vídeo não encontrado");
        }
        Video video = videoOpt.get();

        boolean jaExiste = playlist.getPlaylistVideos().stream()
                .anyMatch(pv -> pv.getVideo().getId_video().equals(videoId));
        if (jaExiste) {
            return ResponseEntity.badRequest().body("Vídeo já está na playlist");
        }

        PlaylistVideoId playlistVideoId = new PlaylistVideoId(playlist.getId_playlist(), video.getId_video());

        PlaylistVideo playlistVideo = new PlaylistVideo();
        playlistVideo.setId(playlistVideoId);
        playlistVideo.setPlaylist(playlist);
        playlistVideo.setVideo(video);
        playlistVideo.setPosicaoVideo(playlist.getPlaylistVideos().size() + 1);
        playlistVideo.setDataAdicao(java.time.LocalDate.now());

        playlistVideoRepository.save(playlistVideo);

        return ResponseEntity.ok("Vídeo adicionado à playlist com sucesso!");
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
}