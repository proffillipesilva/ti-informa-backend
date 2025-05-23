package br.com.tiinforma.backend.repositories;

import br.com.tiinforma.backend.domain.playlist.Playlist;
import br.com.tiinforma.backend.domain.playlist.PlaylistResponseDto;
import br.com.tiinforma.backend.domain.usuario.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    Page<Playlist> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
    List<Playlist> findByUsuario(Usuario usuario);

}
