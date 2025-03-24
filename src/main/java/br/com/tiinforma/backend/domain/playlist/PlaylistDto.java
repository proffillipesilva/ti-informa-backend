package br.com.tiinforma.backend.domain.playlist;

import br.com.tiinforma.backend.domain.playlistVideo.PlaylistVideoDto;
import br.com.tiinforma.backend.domain.enums.Visibilidade;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistDto {
    private Long id;
    private Long idUsuario;
    private String nome;
    private Visibilidade visibilidade;
    private List<PlaylistVideoDto> videos;
}
