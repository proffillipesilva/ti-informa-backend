package br.com.tiinforma.backend.repositories;

import br.com.tiinforma.backend.domain.entities.Assinatura;
import br.com.tiinforma.backend.domain.entities.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video, Long> {
}
