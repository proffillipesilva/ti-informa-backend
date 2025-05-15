package br.com.tiinforma.backend.controller;

import br.com.tiinforma.backend.domain.criador.CriadorCreateDto;
import br.com.tiinforma.backend.domain.criador.CriadorResponseDto;
import br.com.tiinforma.backend.repositories.VideoRepository;
import br.com.tiinforma.backend.services.interfaces.CriadorService;
import br.com.tiinforma.backend.util.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/criador")
@CrossOrigin(origins = "http://localhost:8080")
public class CriadorController {

    @Autowired
    private CriadorService criadorService;

    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-yml"})
    public ResponseEntity<CriadorResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(criadorService.findById(id));
    }

    @GetMapping(produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-yml"})
    public ResponseEntity<List<CriadorResponseDto>> findAll() {
        return ResponseEntity.ok(criadorService.findAll());
    }

    @PostMapping(value = "/register", produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-yml"}, consumes = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-yml"})
    public CriadorResponseDto create(@RequestBody CriadorCreateDto criadorCreateDto) {
        return criadorService.create(criadorCreateDto);
    }

    @PutMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-yml"}, consumes = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-yml"})
    public ResponseEntity<CriadorResponseDto> update(@PathVariable Long id, @RequestBody CriadorCreateDto dto) {
        CriadorResponseDto criadorAtualizado = criadorService.update(id, dto);
        return ResponseEntity.ok(criadorAtualizado);
    }

    @DeleteMapping(value = "/{id}")
    public void delete(@PathVariable Long id) {
        criadorService.delete(id);
    }

    @Autowired
    private VideoRepository videoRepository;
    @GetMapping("/{id}/videos")

    public ResponseEntity<?> listarVideosDoCriador(@PathVariable Long id) {
        var videos = videoRepository.findByCriadorId(id);
        return ResponseEntity.ok(videos);
    }


}


