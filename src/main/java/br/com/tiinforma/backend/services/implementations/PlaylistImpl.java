package br.com.tiinforma.backend.services.implementations;


import br.com.tiinforma.backend.domain.criador.Criador;
import br.com.tiinforma.backend.domain.embeddedPk.PlaylistVideoId;
import br.com.tiinforma.backend.domain.playlist.*;
import br.com.tiinforma.backend.domain.playlistVideo.PlaylistVideo;
import br.com.tiinforma.backend.domain.usuario.Usuario;
import br.com.tiinforma.backend.domain.video.Video;
import br.com.tiinforma.backend.exceptions.ResourceNotFoundException;
import br.com.tiinforma.backend.repositories.CriadorRepository;
import br.com.tiinforma.backend.repositories.PlaylistRepository;
import br.com.tiinforma.backend.repositories.UsuarioRepository;
import br.com.tiinforma.backend.repositories.VideoRepository;
import br.com.tiinforma.backend.services.interfaces.PlaylistService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaylistImpl implements PlaylistService {

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CriadorRepository criadorRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private ModelMapper modelMapper;


    @Override
    public PlaylistResponseDto create(PlaylistCreateDto dto) {
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Criador criador = null;
        if (dto.getCriadorId() != null) {
            criador = criadorRepository.findById(dto.getCriadorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Criador não encontrado"));
        }

        Playlist playlist = modelMapper.map(dto, Playlist.class);
        playlist.setUsuario(usuario);
        playlist.setCriador(criador);

        playlist = playlistRepository.save(playlist);
        return modelMapper.map(playlist, PlaylistResponseDto.class);
    }

    @Override
    public List<PlaylistResponseDto> findAll() {
        return playlistRepository.findAll().
                stream()
                .map(playlist -> modelMapper.map(playlist, PlaylistResponseDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public PlaylistResponseDto findById(Long id) {
        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist não encontrada"));
        return modelMapper.map(playlist, PlaylistResponseDto.class);
    }

    @Override
    public PlaylistResponseDto update(Long id, PlaylistCreateDto dto) {
        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist não encontrada"));

        playlist.setNome(dto.getNome());
        playlist.setVisibilidade(dto.getVisibilidade());

        if (dto.getCriadorId() != null) {
            Criador criador = criadorRepository.findById(dto.getCriadorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Criador não encontrado"));
            playlist.setCriador(criador);
        } else {
            playlist.setCriador(null);
        }

        playlist = playlistRepository.save(playlist);
        return modelMapper.map(playlist, PlaylistResponseDto.class);
    }

    @Override
    public void delete(Long id) {
        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist não encontrada"));
        playlistRepository.delete(playlist);
    }

    @Override
    public PlaylistResponseDto addVideosToPlaylist(Long playlistId, List<Long> videoIds) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist não encontrada"));

        List<Video> videos = videoRepository.findAllById(videoIds);

        int posicaoInicial = playlist.getPlaylistVideos().size() + 1;

        for (int i = 0; i < videos.size(); i++) {
            Video video = videos.get(i);

            PlaylistVideo playlistVideo = PlaylistVideo.builder()
                    .id(new PlaylistVideoId(playlist.getId(), video.getId()))
                    .playlist(playlist)
                    .video(video)
                    .posicaoVideo(posicaoInicial + i)
                    .dataAdicao(LocalDate.now())
                    .build();

            playlist.getPlaylistVideos().add(playlistVideo);
        }

        return modelMapper.map(playlist, PlaylistResponseDto.class);
    }

}