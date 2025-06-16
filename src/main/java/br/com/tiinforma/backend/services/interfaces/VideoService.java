package br.com.tiinforma.backend.services.interfaces;

import br.com.tiinforma.backend.domain.video.Video;
import java.util.List;

public interface VideoService {

    List<Video> buscarVideosPorCriador(Long criadorId);

    List<Video> buscarVideosPopulares();

    List<Video> buscarVideosRecomendados(List<String> interessesUsuario);

    boolean incrementarVisualizacao(Long videoId);

    Long getVisualizacoes(Long videoId);

    void deletarVideo(Long videoId, String username);

    Video buscarVideoPorId(Long videoId);

    void deletarAvaliacoesDoVideo(Long videoId);

    Double calcularMediaAvaliacoes(Long videoId);
}