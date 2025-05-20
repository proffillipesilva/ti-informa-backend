package br.com.tiinforma.backend.services.interfaces;

import br.com.tiinforma.backend.domain.video.Video;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public interface VideoService {

    List<Video> buscarVideosPorCriador(Long criadorId);

}
