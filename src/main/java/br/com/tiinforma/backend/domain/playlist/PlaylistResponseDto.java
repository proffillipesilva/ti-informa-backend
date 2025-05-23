package br.com.tiinforma.backend.domain.playlist;

import br.com.tiinforma.backend.domain.enums.Visibilidade;
import br.com.tiinforma.backend.domain.playlistVideo.PlaylistVideoResponseDto;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistResponseDto {
    private Long id;
    private Long usuarioId;
    private Long criadorId;
    private String nome;
    private Visibilidade visibilidade;
    private List<PlaylistVideoResponseDto> videos;
}
