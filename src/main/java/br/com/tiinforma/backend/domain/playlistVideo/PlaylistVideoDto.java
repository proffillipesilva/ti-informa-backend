package br.com.tiinforma.backend.domain.playlistVideo;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistVideoDto {
    private Long id;
    private Long idVideo;
    private String videoTitulo;
    private Integer posicaoVideo;
}
