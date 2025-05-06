/*package br.com.tiinforma.backend.services.implementations;

import br.com.tiinforma.backend.domain.criador.Criador;
import br.com.tiinforma.backend.domain.video.Video;
import br.com.tiinforma.backend.repositories.VideoRepository;
import br.com.tiinforma.backend.services.aws.StorageService;
import br.com.tiinforma.backend.services.interfaces.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VideoImpl implements VideoService {

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private StorageService storageService;

    public boolean deleteVideo(Long videoId, Criador criadorAutenticado) {
        Optional<Video> videoOptional = videoRepository.findById(videoId);

        if (videoOptional.isPresent()) {
            Video video = videoOptional.get();

            if (video.getCriador().getId().equals(criadorAutenticado.getId())) {
                storageService.deleteFile(video.getUrl());

                videoRepository.delete(video);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
*/