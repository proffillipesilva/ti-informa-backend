package br.com.tiinforma.backend.domain.playlist;

import br.com.tiinforma.backend.domain.enums.Visibilidade;
import br.com.tiinforma.backend.domain.playlistVideo.PlaylistVideoResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistCreateDto {
    private Long id;
    private Long usuarioId;
    private Long criadorId;
    private String nome;
    private Visibilidade visibilidade;
    private List<PlaylistVideoResponseDto> videos;
}
