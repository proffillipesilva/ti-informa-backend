package br.com.tiinforma.backend.domain.playlistVideo;

import jakarta.persistence.Column;
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
    private String videoKey;
    private String videoThumbnail;
    private Integer posicaoVideo;
    private LocalDate dataAdicao;
    private LocalDate dataPublicacao;
}