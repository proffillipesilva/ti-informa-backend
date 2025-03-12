package br.com.tiinforma.backend.domain.playlist;


import br.com.tiinforma.backend.domain.playlistVideo.PlaylistVideoDto;
import br.com.tiinforma.backend.domain.enums.Visibilidade;

import java.util.List;

public record PlaylistDto(
        Long id,
        Long idUsuario,
        String nome,
        Visibilidade visibilidade,
        List<PlaylistVideoDto> videos

) {
}
