package br.com.tiinforma.backend.domain.playlist;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistAddVideosDto {
    private Long playlistId;
    private List<Long> videoIds;
}
