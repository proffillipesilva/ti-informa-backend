package br.com.tiinforma.backend.services.implementations;

import br.com.tiinforma.backend.domain.UsuarioAvaliacao.UsuarioAvaliacao;
import br.com.tiinforma.backend.domain.avaliacao.Avaliacao;
import br.com.tiinforma.backend.domain.video.Video;
import br.com.tiinforma.backend.exceptions.ResourceNotFoundException;
import br.com.tiinforma.backend.repositories.AvaliacaoRepository;
import br.com.tiinforma.backend.repositories.CriadorRepository;
import br.com.tiinforma.backend.repositories.UsuarioAvaliacaoRepository;
import br.com.tiinforma.backend.repositories.VideoRepository;
import br.com.tiinforma.backend.services.aws.StorageService;
import br.com.tiinforma.backend.services.interfaces.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VideoImpl implements VideoService {

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private CriadorRepository criadorRepository;

    @Autowired
    private StorageService storageService;

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private UsuarioAvaliacaoRepository usuarioAvaliacaoRepository;


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

        return recommendedVideos.stream()
                .distinct()
                .collect(Collectors.toList());
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
        }
        return false;
    }

    @Override
    public Long getVisualizacoes(Long videoId) {
        return videoRepository.findById(videoId)
                .map(Video::getVisualizacoes)
                .orElse(null);
    }

    @Override
    @Transactional
    public void deletarAvaliacoesDoVideo(Long videoId) {
        List<UsuarioAvaliacao> usuarioAvaliacoes = usuarioAvaliacaoRepository.findByVideoId(videoId);
        usuarioAvaliacaoRepository.deleteAll(usuarioAvaliacoes);

        List<Avaliacao> avaliacoes = avaliacaoRepository.findByVideoId(videoId);
        avaliacaoRepository.deleteAll(avaliacoes);
    }

    @Override
    @Transactional
    public void deletarVideo(Long videoId, String username) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new ResourceNotFoundException("Vídeo não encontrado"));

        if (!video.getCriador().getEmail().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para excluir este vídeo");
        }

        try {
            deletarAvaliacoesDoVideo(videoId);

            video.getPlaylistVideos().clear();
            videoRepository.saveAndFlush(video);

            storageService.deleteFile(video.getKey(), username);

            if (video.getThumbnail() != null && !video.getThumbnail().isEmpty()) {
                storageService.deleteFile(video.getThumbnail(), username);
            }

            videoRepository.delete(video);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao excluir vídeo", e);
        }
    }

    @Override
    public Video buscarVideoPorId(Long videoId) {
        return videoRepository.findById(videoId)
                .orElseThrow(() -> new ResourceNotFoundException("Vídeo não encontrado"));
    }

    @Override
    public Double calcularMediaAvaliacoes(Long videoId) {
        Double media = videoRepository.calcularMediaAvaliacoes(videoId);
        return media != null ? media : 0.0;
    }

    @Override
    public List<Video> buscarVideosMaisAvaliados() {
        return videoRepository.findAll().stream()
                .filter(v -> v.getAvaliacaoMedia() != null)
                .sorted((v1, v2) -> Double.compare(v2.getAvaliacaoMedia(), v1.getAvaliacaoMedia()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Video> buscarVideosRecentes() {
        return videoRepository.findAllOrderByDataPublicacaoDesc();
    }

}