package br.com.tiinforma.backend.services.interfaces;

import br.com.tiinforma.backend.domain.playlist.PlaylistCreateDto;
import br.com.tiinforma.backend.domain.playlist.PlaylistResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;

public interface PlaylistService {

    PlaylistResponseDto create(PlaylistCreateDto dto);

    List<PlaylistResponseDto> findAll();

    PlaylistResponseDto findById(Long id);

    PlaylistResponseDto update(Long id, PlaylistCreateDto dto);

    void delete(Long id);

    PlaylistResponseDto addVideosToPlaylist(Long playlistId, List<Long> videoIds);

}
