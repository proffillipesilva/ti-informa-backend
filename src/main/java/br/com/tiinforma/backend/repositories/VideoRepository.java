package br.com.tiinforma.backend.repositories;

import br.com.tiinforma.backend.domain.video.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VideoRepository extends JpaRepository<Video, Long> {
    Video findByKey(String key);
    List<Video> findByCriadorId(Long criadorId);

    List<Video> findAllByOrderByVisualizacoesDesc();

    @Query("SELECT v FROM Video v WHERE v.categoria = :interesse OR v.palavraChave LIKE %:interesse%")
    List<Video> findByCategoriaOrPalavraChaveContaining(@Param("interesse") String interesse);
}