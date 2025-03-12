package br.com.tiinforma.backend.domain.playlistVideo;

import br.com.tiinforma.backend.domain.embeddedPk.PlaylistVideoId;
import br.com.tiinforma.backend.domain.playlist.Playlist;
import br.com.tiinforma.backend.domain.video.Video;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "playlist_video")
public class PlaylistVideo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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