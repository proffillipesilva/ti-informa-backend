package br.com.tiinforma.backend.repositories;

import br.com.tiinforma.backend.domain.video.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VideoRepository extends JpaRepository<Video, Long> {
    Video findByKey(String key);
    @Query("SELECT v FROM Video v WHERE v.criador.id = :criadorId")
    List<Video> findByCriadorId(@Param("criadorId") Long criadorId);

    List<Video> findAllByOrderByVisualizacoesDesc();

    @Query("SELECT v FROM Video v WHERE v.categoria = :interesse OR v.palavraChave LIKE %:interesse%")
    List<Video> findByCategoriaOrPalavraChaveContaining(@Param("interesse") String interesse);

    @Query("SELECT AVG(a.nota) FROM Avaliacao a WHERE a.video.id = :videoId")
    Double calcularMediaAvaliacoes(@Param("videoId") Long videoId);

    @Query("SELECT v FROM Video v ORDER BY v.dataPublicacao DESC")
    List<Video> findAllOrderByDataPublicacaoDesc();
}