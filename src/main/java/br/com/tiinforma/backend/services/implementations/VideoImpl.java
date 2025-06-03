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
    public void incrementarVisualizacao(Long videoId) {
        Optional<Video> videoOptional = videoRepository.findById(videoId);
        if (videoOptional.isPresent()) {
            Video video = videoOptional.get();
            video.setVisualizacoes(video.getVisualizacoes() + 1);
            videoRepository.save(video);
        } else {
        }
    }
}