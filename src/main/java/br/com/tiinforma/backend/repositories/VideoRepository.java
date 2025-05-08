package br.com.tiinforma.backend.repositories;

import br.com.tiinforma.backend.domain.video.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video, Long> {
    Video findByKey(String key);
}
