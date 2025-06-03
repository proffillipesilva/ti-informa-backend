package br.com.tiinforma.backend.services.db;

import br.com.tiinforma.backend.domain.avaliacao.Avaliacao;
import br.com.tiinforma.backend.domain.assinatura.Assinatura;
import br.com.tiinforma.backend.domain.criador.Criador;
import br.com.tiinforma.backend.domain.enums.Funcao;
import br.com.tiinforma.backend.domain.enums.Plano;
import br.com.tiinforma.backend.domain.enums.Visibilidade;
import br.com.tiinforma.backend.domain.playlist.Playlist;
import br.com.tiinforma.backend.domain.userDetails.UserDetailsImpl;
import br.com.tiinforma.backend.domain.usuario.Usuario;
import br.com.tiinforma.backend.domain.usuario.UsuarioCreateDto;
import br.com.tiinforma.backend.repositories.*;
import br.com.tiinforma.backend.domain.video.Video;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class DBService {

    @Autowired
    AssinaturaRepository assinaturaRepository;

    @Autowired
    AvaliacaoRepository avaliacaoRepository;

    @Autowired
    CriadorRepository criadorRepository;

    @Autowired
    PlaylistRepository playlistRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    VideoRepository videoRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostConstruct
    public void initDatabase() {
        if (usuarioRepository.count() == 0) {
            criarUsuarios();
            criarCriadores();
            criarAssinaturas();
            criarPlaylists();
            criarVideos();
            criarAvaliacoes();
        }
    }

    private void criarUsuarios() {
        List<Usuario> usuarios = List.of(
                Usuario.builder()
                        .nome("Ana Silva")
                        .email("ana.silva@email.com")
                        .senha(passwordEncoder.encode("senha123"))
                        .interesses("Rock, Jazz")
                        .funcao(Funcao.USUARIO)
                        .build(),
                Usuario.builder()
                        .nome("Carlos Oliveira")
                        .email("carlos.oliveira@email.com")
                        .senha(passwordEncoder.encode("senha456"))
                        .interesses("Eletrônica, Techno")
                        .funcao(Funcao.USUARIO)
                        .build(),
                Usuario.builder()
                        .nome("Samuel Raymundo")
                        .email("samuel@email.com")
                        .senha(passwordEncoder.encode("senha1234"))
                        .funcao(Funcao.ADMINISTRADOR)
                        .build()
        );

        for (Usuario u : usuarios) {
            boolean existeEmail = usuarioRepository.findByEmail(u.getEmail()).isPresent();
            if (!existeEmail) {
                usuarioRepository.save(u);
            }
        }
    }

    private void criarCriadores() {
        List<Criador> criadores = List.of(
                Criador.builder()
                        .nome("Lucas FilmMaker")
                        .email("lucas.filmmaker@email.com")
                        .cpf("12345678901")
                        .senha(passwordEncoder.encode("senha123"))
                        .formacao("Cinema e Audiovisual")
                        .funcao(Funcao.CRIADOR)
                        .build(),
                Criador.builder()
                        .nome("Beatriz MusicPro")
                        .email("beatriz.music@email.com")
                        .cpf("98765432109")
                        .senha(passwordEncoder.encode("senha456"))
                        .formacao("Produção Musical")
                        .funcao(Funcao.CRIADOR)
                        .build()
        );

        for (Criador c : criadores) {
            boolean existeCpf = criadorRepository.findByCpf(c.getCpf()).isPresent();
            boolean existeEmail = criadorRepository.findByEmail(c.getEmail()).isPresent();
            if (!existeCpf && !existeEmail) {
                criadorRepository.save(c);
            }
        }
    }

    private void criarAssinaturas() {
        List<Usuario> usuarios = usuarioRepository.findAll();

        List<Assinatura> assinaturas = List.of(
                Assinatura.builder()
                        .plano(Plano.PREMIUM)
                        .dataInicio(LocalDate.now())
                        .dataFim(LocalDate.now().plusYears(1))
                        .preco(29.99)
                        .usuario(usuarios.get(0))
                        .build(),
                Assinatura.builder()
                        .plano(Plano.FAMILIA)
                        .dataInicio(LocalDate.now())
                        .dataFim(LocalDate.now().plusYears(1))
                        .preco(49.99)
                        .usuario(usuarios.get(1))
                        .build()
        );
        assinaturaRepository.saveAll(assinaturas);
    }

    private void criarVideos() {
        List<Criador> criadores = criadorRepository.findAll();
        List<Video> videos = List.of(
                Video.builder()
                        .titulo("Como Editar Vídeos Profissionais")
                        .descricao("Aprenda técnicas avançadas de edição.")
                        .dataPublicacao(LocalDate.now())
                        .categoria("Cinema")
                        .palavraChave("edição,cinema")
                        .key("video123.mp4")
                        .criador(criadores.get(0))
                        .build(),
                Video.builder()
                        .titulo("Teoria Musical Básica")
                        .descricao("Introdução à teoria musical.")
                        .categoria("Música")
                        .palavraChave("música,teoria")
                        .key("music456.mp4")
                        .criador(criadores.get(1))
                        .build()
        );
        videoRepository.saveAll(videos);
    }

    private void criarAvaliacoes() {
        List<Video> videos = videoRepository.findAll();
        List<Avaliacao> avaliacoes = List.of(
                Avaliacao.builder()
                        .nota(5)
                        .comentario("Conteúdo incrível, muito detalhado!")
                        .build(),
                Avaliacao.builder()
                        .nota(4)
                        .comentario("Bom, mas poderia ter exemplos práticos.")
                        .build()
        );
        avaliacaoRepository.saveAll(avaliacoes);
    }

    private void criarPlaylists() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        List<Criador> criadores = criadorRepository.findAll();
        List<Video> videos = videoRepository.findAll();

        List<Playlist> playlists = List.of(
                Playlist.builder()
                        .nome("Rock Classics")
                        .visibilidade(Visibilidade.PUBLICA)
                        .usuario(usuarios.get(0))
                        .criador(criadores.get(0))
                        .build(),
                Playlist.builder()
                        .nome("Jazz Nights")
                        .visibilidade(Visibilidade.PRIVADA)
                        .usuario(usuarios.get(0))
                        .criador(criadores.get(1))
                        .build()
        );
        playlistRepository.saveAll(playlists);
    }

    // Função para atualizar a descrição do usuário
    public void atualizarDescricaoUsuario(Long usuarioId, String descricao) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(usuarioId);
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            usuario.setDescricao(descricao);
            usuarioRepository.save(usuario);
        } else {
            throw new RuntimeException("Usuário não encontrado");
        }
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
        usuario.setSenha(passwordEncoder.encode(usuarioDto.getSenha()));
        usuario.setInteresses(usuarioDto.getInteresses());
        usuario.setDescricao(usuarioDto.getDescricao());
        usuarioRepository.save(usuario);
        return ResponseEntity.ok("Cadastro atualizado com sucesso!");
    }
}