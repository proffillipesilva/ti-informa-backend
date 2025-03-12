package br.com.tiinforma.backend.repositories;

import br.com.tiinforma.backend.domain.playlistVideo.PlaylistVideo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistVideoRepository extends JpaRepository<PlaylistVideo, Long> {
}

