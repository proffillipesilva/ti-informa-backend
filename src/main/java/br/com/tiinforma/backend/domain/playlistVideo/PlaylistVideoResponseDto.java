package br.com.tiinforma.backend.domain.playlistVideo;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistVideoResponseDto {
    private Long videoId;
    private String titulo;
    private Integer posicaoVideo;
    private LocalDate dataAdicao;
}
