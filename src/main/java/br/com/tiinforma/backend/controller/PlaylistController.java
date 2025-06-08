package br.com.tiinforma.backend.controller;



import br.com.tiinforma.backend.domain.embeddedPk.PlaylistVideoId;
import br.com.tiinforma.backend.domain.enums.Visibilidade;
import br.com.tiinforma.backend.domain.playlist.Playlist;
import br.com.tiinforma.backend.domain.playlist.PlaylistAddVideosDto;
import br.com.tiinforma.backend.domain.playlist.PlaylistCreateDto;
import br.com.tiinforma.backend.domain.playlist.PlaylistResponseDto;

import br.com.tiinforma.backend.domain.playlistVideo.PlaylistVideo;
import br.com.tiinforma.backend.domain.playlistVideo.PlaylistVideoResponseDto;
import br.com.tiinforma.backend.domain.userDetails.UserDetailsImpl;
import br.com.tiinforma.backend.domain.usuario.Usuario;
import br.com.tiinforma.backend.domain.video.Video;
import br.com.tiinforma.backend.exceptions.ResourceNotFoundException;
import br.com.tiinforma.backend.repositories.PlaylistRepository;
import br.com.tiinforma.backend.repositories.PlaylistVideoRepository;
import br.com.tiinforma.backend.repositories.UsuarioRepository;
import br.com.tiinforma.backend.repositories.VideoRepository;
import br.com.tiinforma.backend.services.implementations.PlaylistPaginacaoService;
import br.com.tiinforma.backend.services.interfaces.PlaylistService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/playlists")
@RequiredArgsConstructor
public class PlaylistController {

    @Autowired
    private PlaylistService playlistService;

    @Autowired
    private PlaylistPaginacaoService playlistPaginacaoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private PlaylistVideoRepository playlistVideoRepository;

    @PostMapping("/criar")
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
        PlaylistCreateDto dto = new PlaylistCreateDto(
                playlist.getId(),
                usuario.getId(),
                null,
                playlist.getNome(),
                playlist.getVisibilidade(),
                List.of()
        );
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<PlaylistResponseDto>> findAll() {
        return ResponseEntity.ok(playlistService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaylistResponseDto> findById(@PathVariable Long id) {
        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist não encontrada"));

        List<PlaylistVideoResponseDto> videos = playlist.getPlaylistVideos().stream()
                .sorted(Comparator.comparing(PlaylistVideo::getPosicaoVideo))
                .map(pv -> new PlaylistVideoResponseDto(
                        pv.getVideo().getId(),
                        pv.getVideo().getTitulo(),
                        pv.getVideo().getKey(),
                        pv.getPosicaoVideo(),
                        pv.getDataAdicao()
                ))
                .collect(Collectors.toList());

        PlaylistResponseDto dto = new PlaylistResponseDto(
                playlist.getId(),
                playlist.getUsuario().getId(),
                playlist.getCriador() != null ? playlist.getCriador().getId() : null,
                playlist.getNome(),
                playlist.getVisibilidade(),
                videos
        );

        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlaylistResponseDto> update(@PathVariable Long id, @RequestBody PlaylistCreateDto dto) {
        return ResponseEntity.ok(playlistService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        playlistService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public Page<PlaylistResponseDto> searchByName(@RequestParam String nome, Pageable pageable) {
        return playlistPaginacaoService.findByNome(nome, pageable);
    }

    @GetMapping("/minhas-playlists")
    public ResponseEntity<?> listarMinhasPlaylists(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Usuario usuario = usuarioRepository.findById(userDetails.getId()).orElse(null);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não encontrado.");
        }
        List<Playlist> playlists = playlistRepository.findByUsuario(usuario);
        List<PlaylistResponseDto> dtos = playlists.stream().map(playlist -> new PlaylistResponseDto(
                playlist.getId(),
                usuario.getId(),
                playlist.getCriador() != null ? playlist.getCriador().getId() : null,
                playlist.getNome(),
                playlist.getVisibilidade(),
                playlist.getPlaylistVideos().stream().map(pv -> new PlaylistVideoResponseDto(
                        pv.getVideo().getId(),
                        pv.getVideo().getTitulo(),
                        pv.getVideo().getKey(),
                        pv.getPosicaoVideo(),
                        pv.getDataAdicao()
                )).collect(Collectors.toList())
        )).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/{playlistId}/adicionar-video")
    public ResponseEntity<?> adicionarVideoNaPlaylist(
            @PathVariable Long playlistId,
            @RequestParam Long videoId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Optional<Playlist> playlistOpt = playlistRepository.findById(playlistId);
        if (playlistOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Playlist não encontrada.");
        }
        Playlist playlist = playlistOpt.get();

        if (!playlist.getUsuario().getId().equals(userDetails.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Você não tem permissão para modificar esta playlist.");
        }

        Optional<Video> videoOpt = videoRepository.findById(videoId);
        if (videoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vídeo não encontrado.");
        }
        Video video = videoOpt.get();

        boolean videoJaExiste = playlist.getPlaylistVideos().stream()
                .anyMatch(pv -> pv.getVideo().getId().equals(videoId));

        if (videoJaExiste) {
            return ResponseEntity.ok("O vídeo já está na playlist.");
        }

        int proximaPosicao = playlist.getPlaylistVideos().stream()
                .mapToInt(PlaylistVideo::getPosicaoVideo)
                .max()
                .orElse(0) + 1;

        PlaylistVideo playlistVideo = PlaylistVideo.builder()
                .id(new PlaylistVideoId(playlistId, videoId))
                .playlist(playlist)
                .video(video)
                .posicaoVideo(proximaPosicao)
                .dataAdicao(java.time.LocalDate.now())
                .build();

        playlistVideoRepository.save(playlistVideo);
        playlist.getPlaylistVideos().add(playlistVideo);

        PlaylistResponseDto responseDto = new PlaylistResponseDto(
                playlist.getId(),
                playlist.getUsuario().getId(),
                playlist.getCriador() != null ? playlist.getCriador().getId() : null,
                playlist.getNome(),
                playlist.getVisibilidade(),
                playlist.getPlaylistVideos().stream()
                        .map(pv -> new PlaylistVideoResponseDto(
                                pv.getVideo().getId(),
                                pv.getVideo().getTitulo(),
                                pv.getVideo().getKey(),
                                pv.getPosicaoVideo(),
                                pv.getDataAdicao()
                        ))
                        .collect(Collectors.toList())
        );

        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{id}/visibilidade")
    public ResponseEntity<PlaylistResponseDto> updateVisibilidade(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist não encontrada"));

        if (!playlist.getUsuario().getId().equals(userDetails.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        String visibilidadeStr = body.get("visibilidade");
        if (visibilidadeStr == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            Visibilidade visibilidade = Visibilidade.valueOf(visibilidadeStr);
            playlist.setVisibilidade(visibilidade);
            playlistRepository.save(playlist);

            return ResponseEntity.ok(new PlaylistResponseDto(
                    playlist.getId(),
                    playlist.getUsuario().getId(),
                    playlist.getCriador() != null ? playlist.getCriador().getId() : null,
                    playlist.getNome(),
                    playlist.getVisibilidade(),
                    playlist.getPlaylistVideos().stream()
                            .map(pv -> new PlaylistVideoResponseDto(
                                    pv.getVideo().getId(),
                                    pv.getVideo().getTitulo(),
                                    pv.getVideo().getKey(),
                                    pv.getPosicaoVideo(),
                                    pv.getDataAdicao()
                            ))
                            .collect(Collectors.toList())
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
