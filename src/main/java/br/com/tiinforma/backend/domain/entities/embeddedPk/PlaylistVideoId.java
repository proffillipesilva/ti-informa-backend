package br.com.tiinforma.backend.domain.entities.embeddedPk;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PlaylistVideoId implements Serializable {
    private Long idPlaylist;
    private Long idVideo;

}