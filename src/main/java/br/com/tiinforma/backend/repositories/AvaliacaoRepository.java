package br.com.tiinforma.backend.repositories;

import br.com.tiinforma.backend.domain.avaliacao.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {
    Optional<Avaliacao> findByUsuarioIdAndVideoId(Long usuarioId, Long videoId);

    boolean existsByUsuarioIdAndVideoId(Long usuarioId, Long videoId);
}