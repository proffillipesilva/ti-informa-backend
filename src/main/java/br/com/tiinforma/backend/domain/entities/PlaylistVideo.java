package br.com.tiinforma.backend.domain.entities;

import br.com.tiinforma.backend.domain.entities.embeddedPk.PlaylistVideoId;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "playlist_video")
public class PlaylistVideo {

    @EmbeddedId
    private PlaylistVideoId id;

    @ManyToOne
    @MapsId("idPlaylist")
    @JoinColumn(name = "id_playlist")
    private Playlist playlist;

    @ManyToOne
    @MapsId("idVideo")
    @JoinColumn(name = "id_video")
    private Video video;

    private LocalDate dataAdicao;

    @Column(name = "posicao_video")
    private Integer posicaoVideo;


}