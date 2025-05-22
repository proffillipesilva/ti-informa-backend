package br.com.tiinforma.backend.domain.playlist;

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
    private String nome;
    private String visibilidade;
    private Long usuarioId;
    private String nomeCriador;
    private List<PlaylistVideoResponseDto> videos;
}
