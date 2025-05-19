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
    private Long idVideo;
    private String videoTitulo;
    private String videoKey;
    private Integer posicaoVideo;
}
