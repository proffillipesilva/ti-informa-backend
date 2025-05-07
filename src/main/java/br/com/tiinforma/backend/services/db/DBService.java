package br.com.tiinforma.backend.services.db;

import br.com.tiinforma.backend.domain.avaliacao.Avaliacao;
import br.com.tiinforma.backend.domain.assinatura.Assinatura;
import br.com.tiinforma.backend.domain.criador.Criador;
import br.com.tiinforma.backend.domain.enums.Funcao;
import br.com.tiinforma.backend.domain.enums.Plano;
import br.com.tiinforma.backend.domain.enums.Visibilidade;
import br.com.tiinforma.backend.domain.playlist.Playlist;
import br.com.tiinforma.backend.domain.usuario.Usuario;
import br.com.tiinforma.backend.domain.video.Video;
import br.com.tiinforma.backend.repositories.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

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
                        .senha(passwordEncoder.encode("senha123")) // Criptografando senha
                        .interesses("Rock, Jazz")
                        .funcao(Funcao.USUARIO)
                        .build(),
                Usuario.builder()
                        .nome("Carlos Oliveira")
                        .email("carlos.oliveira@email.com")
                        .senha(passwordEncoder.encode("senha456")) // Criptografando senha
                        .interesses("Eletrônica, Techno")
                        .funcao(Funcao.USUARIO)
                        .build()
        );
        usuarioRepository.saveAll(usuarios);
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
        criadorRepository.saveAll(criadores);
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
                        .palavraChave(List.of("edição", "cinema"))
                        .criador(criadores.get(0)) // Lucas FilmMaker
                        .build(),
                Video.builder()
                        .titulo("Teoria Musical Básica")
                        .descricao("Introdução à teoria musical.")
                        .categoria("Música")
                        .palavraChave(List.of("música", "teoria"))
                        .criador(criadores.get(1)) // Beatriz MusicPro
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
                        .visibilidade(Visibilidade.PUBLICO)
                        .usuario(usuarios.get(0))
                        .criador(criadores.get(0))
                        .build(),
                Playlist.builder()
                        .nome("Jazz Nights")
                        .visibilidade(Visibilidade.PRIVADO)
                        .usuario(usuarios.get(0))
                        .criador(criadores.get(1))
                        .build()
        );
        playlistRepository.saveAll(playlists);
    }
}