package br.com.tiinforma.backend.repositories;

import br.com.tiinforma.backend.domain.playlist.Playlist;
import br.com.tiinforma.backend.domain.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    List<Playlist> findByUsuario(Usuario usuario);
}