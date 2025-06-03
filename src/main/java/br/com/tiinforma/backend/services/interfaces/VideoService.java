package br.com.tiinforma.backend.services.interfaces;

import br.com.tiinforma.backend.domain.video.Video;
import java.util.List;

public interface VideoService {

    List<Video> buscarVideosPorCriador(Long criadorId);

    List<Video> buscarVideosPopulares();

    List<Video> buscarVideosRecomendados(List<String> interessesUsuario);

    void incrementarVisualizacao(Long videoId);
}