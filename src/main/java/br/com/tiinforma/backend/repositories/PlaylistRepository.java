package br.com.tiinforma.backend.repositories;

import br.com.tiinforma.backend.domain.entities.Assinatura;
import br.com.tiinforma.backend.domain.entities.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
}
