package br.com.tiinforma.backend.services.implementations;

import br.com.tiinforma.backend.domain.playlist.Playlist;
import br.com.tiinforma.backend.domain.playlist.PlaylistResponseDto;
import br.com.tiinforma.backend.repositories.PlaylistRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PlaylistPaginacaoService {

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private ModelMapper modelMapper;

    public Page<PlaylistResponseDto> findByNome(String nome, Pageable pageable) {
        Page<Playlist> page = playlistRepository.findByNomeContainingIgnoreCase(nome, pageable);
        return page.map(this::mapToResponseDto);
    }

    private PlaylistResponseDto mapToResponseDto(Playlist playlist) {
        return modelMapper.map(playlist, PlaylistResponseDto.class);
    }
}
