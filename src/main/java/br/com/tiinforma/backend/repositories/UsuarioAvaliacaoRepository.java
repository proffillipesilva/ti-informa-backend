package br.com.tiinforma.backend.repositories;

import br.com.tiinforma.backend.domain.UsuarioAvaliacao.UsuarioAvaliacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioAvaliacaoRepository extends JpaRepository<UsuarioAvaliacao, Long> {
    @Query("SELECT ua FROM UsuarioAvaliacao ua WHERE ua.usuario.id = :usuarioId AND ua.video.id = :videoId")
    Optional<UsuarioAvaliacao> findByUsuarioIdAndVideoId(@Param("usuarioId") Long usuarioId, @Param("videoId") Long videoId);

    boolean existsByUsuarioIdAndVideoId(Long usuarioId, Long videoId);

    List<UsuarioAvaliacao> findByAvaliacaoId(Long avaliacaoId);

    List<UsuarioAvaliacao> findByVideoId(Long videoId);
}