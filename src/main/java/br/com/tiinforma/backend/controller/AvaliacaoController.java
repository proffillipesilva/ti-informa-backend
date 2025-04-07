package br.com.tiinforma.backend.controller;

import br.com.tiinforma.backend.domain.avaliacao.AvaliacaoCreateDto;
import br.com.tiinforma.backend.domain.avaliacao.AvaliacaoDto;
import br.com.tiinforma.backend.services.interfaces.AvaliacaoService;
import br.com.tiinforma.backend.util.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/avaliacoes")
@CrossOrigin(origins = "http://localhost:8080")
public class AvaliacaoController {

    @Autowired
    private AvaliacaoService avaliacaoService;

    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-yml"})
    public ResponseEntity<AvaliacaoDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(avaliacaoService.findById(id));
    }

    @GetMapping(produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-yml"})
    public ResponseEntity<List<AvaliacaoDto>> findAll() {
        return ResponseEntity.ok(avaliacaoService.findAll());
    }

    @PostMapping(value = "/create", produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-yml"},
            consumes = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-yml"})
    public AvaliacaoDto create(@RequestBody AvaliacaoCreateDto avaliacaoCreateDto) {
        return avaliacaoService.create(avaliacaoCreateDto);
    }

    @PutMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-yml"},
            consumes = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-yml"})
    public ResponseEntity<AvaliacaoCreateDto> update(@PathVariable Long id, @RequestBody AvaliacaoCreateDto dto) {
        dto.setUserId(id);
        AvaliacaoCreateDto atualizado = avaliacaoService.update(dto);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        avaliacaoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
