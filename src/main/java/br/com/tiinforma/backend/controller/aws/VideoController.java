package br.com.tiinforma.backend.controller.aws;


import br.com.tiinforma.backend.domain.video.Video;
import br.com.tiinforma.backend.services.interfaces.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/file")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @GetMapping("/buscar-videos")
    public ResponseEntity<List<Video>> buscarVideos(@RequestParam("termo") String termo) {
        List<Video> resultados = videoService.buscarVideosPorTermo(termo);
        return ResponseEntity.ok(resultados);
    }

}
