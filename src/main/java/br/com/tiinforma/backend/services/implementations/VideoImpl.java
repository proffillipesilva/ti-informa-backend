package br.com.tiinforma.backend.services.implementations;

import br.com.tiinforma.backend.domain.video.Video;
import br.com.tiinforma.backend.repositories.VideoRepository;
import br.com.tiinforma.backend.services.interfaces.VideoService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class VideoImpl implements VideoService {

    @Autowired
    private VideoRepository videoRepository;

    @Override
    public List<Video> buscarVideosPorCriador(Long criadorId) {
        return videoRepository.findByCriadorId(criadorId);
    }
}
