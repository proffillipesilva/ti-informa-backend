package br.com.tiinforma.backend.repositories;

import br.com.tiinforma.backend.domain.avaliacao.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {
    Optional<Avaliacao> findByUsuarioIdAndVideoId(Long usuarioId, Long videoId);

    boolean existsByUsuarioIdAndVideoId(Long usuarioId, Long videoId);

    List<Avaliacao> findByVideoId(Long videoId);

    @Query("SELECT AVG(a.nota) FROM Avaliacao a WHERE a.video.id = :videoId")
    Double calcularMediaAvaliacoes(@Param("videoId") Long videoId);
}