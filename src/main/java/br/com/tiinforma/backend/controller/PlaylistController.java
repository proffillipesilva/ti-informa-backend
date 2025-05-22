package br.com.tiinforma.backend.controller;



import br.com.tiinforma.backend.domain.playlist.PlaylistAddVideosDto;
import br.com.tiinforma.backend.domain.playlist.PlaylistCreateDto;
import br.com.tiinforma.backend.domain.playlist.PlaylistResponseDto;

import br.com.tiinforma.backend.services.implementations.PlaylistPaginacaoService;
import br.com.tiinforma.backend.services.interfaces.PlaylistService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/playlists")
@RequiredArgsConstructor
public class PlaylistController {

    @Autowired
    private PlaylistService playlistService;

    @Autowired
    private PlaylistPaginacaoService playlistPaginacaoService;

    @PostMapping
    public ResponseEntity<PlaylistResponseDto> create(@RequestBody PlaylistCreateDto dto) {
        return ResponseEntity.ok(playlistService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<PlaylistResponseDto>> findAll() {
        return ResponseEntity.ok(playlistService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaylistResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(playlistService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlaylistResponseDto> update(@PathVariable Long id, @RequestBody PlaylistCreateDto dto) {
        return ResponseEntity.ok(playlistService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        playlistService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public Page<PlaylistResponseDto> searchByName(@RequestParam String nome, Pageable pageable) {
        return playlistPaginacaoService.findByNome(nome, pageable);
    }

    @PostMapping("/{id}/add-videos")
    public ResponseEntity<PlaylistResponseDto> addVideos(
            @PathVariable Long id,
            @RequestBody PlaylistAddVideosDto dto
    ) {
        return ResponseEntity.ok(playlistService.addVideosToPlaylist(id, dto.getVideoIds()));
    }


}
