package br.com.tiinforma.backend.services.implementations;

import br.com.tiinforma.backend.domain.video.Video;
import br.com.tiinforma.backend.repositories.VideoRepository;
import br.com.tiinforma.backend.services.interfaces.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VideoImpl implements VideoService {

    @Autowired
    private VideoRepository videoRepository;

    @Override
    public List<Video> buscarVideosPorCriador(Long criadorId) {
        return videoRepository.findByCriadorId(criadorId);
    }

    @Override
    public List<Video> buscarVideosPopulares() {
        return videoRepository.findAllByOrderByVisualizacoesDesc();
    }

    @Override
    public List<Video> buscarVideosRecomendados(List<String> interessesUsuario) {
        if (interessesUsuario == null || interessesUsuario.isEmpty()) {
            return buscarVideosPopulares();
        }

        List<Video> recommendedVideos = new ArrayList<>();
        for (String interesse : interessesUsuario) {
            recommendedVideos.addAll(videoRepository.findByCategoriaOrPalavraChaveContaining(interesse));
        }

        return recommendedVideos.stream().distinct().collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean incrementarVisualizacao(Long videoId) {
        Optional<Video> videoOptional = videoRepository.findById(videoId);
        if (videoOptional.isPresent()) {
            Video video = videoOptional.get();
            long currentViews = (video.getVisualizacoes() != null) ? video.getVisualizacoes() : 0L;
            video.setVisualizacoes(currentViews + 1);
            videoRepository.save(video);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Long getVisualizacoes(Long videoId) {
        Optional<Video> videoOptional = videoRepository.findById(videoId);
        return videoOptional.map(Video::getVisualizacoes).orElse(null);
    }
}