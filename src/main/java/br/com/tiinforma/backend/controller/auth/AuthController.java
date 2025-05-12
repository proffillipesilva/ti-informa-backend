package br.com.tiinforma.backend.controller.auth;

import br.com.tiinforma.backend.domain.criador.Criador;
import br.com.tiinforma.backend.domain.criador.CriadorCreateDto;
import br.com.tiinforma.backend.domain.dtosComuns.login.AuthLoginDto;
import br.com.tiinforma.backend.domain.dtosComuns.login.LoginResponseDto;
import br.com.tiinforma.backend.domain.enums.Funcao;
import br.com.tiinforma.backend.domain.enums.Visibilidade;
import br.com.tiinforma.backend.domain.playlist.Playlist;
import br.com.tiinforma.backend.domain.playlist.PlaylistDto;
import br.com.tiinforma.backend.domain.playlistVideo.PlaylistVideoDto;
import br.com.tiinforma.backend.domain.userDetails.UserDetailsImpl;
import br.com.tiinforma.backend.domain.usuario.Usuario;
import br.com.tiinforma.backend.domain.usuario.UsuarioCreateDto;
import br.com.tiinforma.backend.domain.video.Video;
import br.com.tiinforma.backend.repositories.CriadorRepository;
import br.com.tiinforma.backend.repositories.PlaylistRepository;
import br.com.tiinforma.backend.repositories.UsuarioRepository;
import br.com.tiinforma.backend.repositories.VideoRepository;
import br.com.tiinforma.backend.security.jwt.TokenService;
import br.com.tiinforma.backend.services.aws.StorageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        criador.setFormacao(criadorDto.getFormacao());

        criadorRepository.save(criador);

        return ResponseEntity.ok("Cadastro de criador atualizado com sucesso!");
    }

    @GetMapping("/me")
    public ResponseEntity<?> getLoggedInUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails != null) {
            Usuario usuario = usuarioRepository.findById(userDetails.getId()).orElse(null);
            Criador criador = criadorRepository.findByEmail(userDetails.getUsername()).orElse(null);

            if (usuario != null) {
                return ResponseEntity.ok(Map.of("nome", usuario.getNome(), "email", usuario.getEmail(), "isCriador", (criador != null)));
            } else if (criador != null) {
                return ResponseEntity.ok(Map.of("nome", criador.getNome(), "email", criador.getEmail(), "isCriador", true));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Não autenticado");
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadVideo(
            @RequestParam("file") MultipartFile file,
            @RequestParam("titulo") String titulo,
            @RequestParam("descricao") String descricao,
            @RequestParam(value = "categoria", required = false) String categoria,
            @RequestParam(value = "palavraChave", required = false) List<String> palavraChave,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        if (userDetails.getFuncao() != Funcao.CRIADOR) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Apenas criadores podem enviar vídeos.");
        }
        Criador criador = criadorRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Criador não encontrado"));
        if (palavraChave == null) palavraChave = List.of();
        String result = storageService.uploadFile(
                file, titulo, descricao, categoria, LocalDate.now(), palavraChave, criador
        );
        return ResponseEntity.ok(result);
    }

    @GetMapping("/meus-videos")
    public ResponseEntity<?> listarMeusVideos(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            if (userDetails == null || userDetails.getUsername() == null) {
                System.err.println("listarMeusVideos: UserDetails ou Username é nulo.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Não autenticado.");
            }
            System.out.println("listarMeusVideos: Username do UserDetails: " + userDetails.getUsername());
            Criador criador = criadorRepository.findByEmail(userDetails.getUsername()).orElse(null);
            if (criador == null) {
                System.err.println("listarMeusVideos: Criador não encontrado para o email: " + userDetails.getUsername());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Apenas criadores podem ver seus vídeos.");
            }
            System.out.println("listarMeusVideos: Criador encontrado: " + criador.getNome());
            List<Video> videos = videoRepository.findByCriador(criador);
            System.out.println("listarMeusVideos: Encontrados " + videos.size() + " vídeos para o criador.");
            return ResponseEntity.ok(videos);
        } catch (Exception e) {
            System.err.println("listarMeusVideos: Um erro inesperado ocorreu: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao buscar vídeos do criador.");
        }
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
                playlist.getId(),
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
                playlist.getId(),
                usuario.getId_usuario(),
                playlist.getNome(),
                playlist.getVisibilidade(),
                playlist.getPlaylistVideos().stream().map(pv -> new PlaylistVideoDto(
                        pv.getVideo().getId_video(),
                        pv.getVideo().getId_video(),
                        pv.getVideo().getTitulo(),
                        pv.getPosicaoVideo()
                )).toList()
        )).toList();
        return ResponseEntity.ok(dtos);
    }
}