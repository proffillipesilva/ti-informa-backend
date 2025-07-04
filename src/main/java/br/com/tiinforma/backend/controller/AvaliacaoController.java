package br.com.tiinforma.backend.controller;

import br.com.tiinforma.backend.domain.avaliacao.AvaliacaoCreateDto;
import br.com.tiinforma.backend.domain.avaliacao.AvaliacaoModel;
import br.com.tiinforma.backend.domain.avaliacao.AvaliacaoModelAssembler;
import br.com.tiinforma.backend.domain.avaliacao.AvaliacaoResponseDto;
import br.com.tiinforma.backend.services.interfaces.AvaliacaoService;
import br.com.tiinforma.backend.util.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/avaliacoes")
public class AvaliacaoController {

    @Autowired
    private AvaliacaoService avaliacaoService;

    @Autowired
    private AvaliacaoModelAssembler avaliacaoModelAssembler;

    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-yml"})
    public ResponseEntity<AvaliacaoModel> findById(@PathVariable Long id) {
        AvaliacaoResponseDto dto = avaliacaoService.findById(id);
        AvaliacaoModel model = avaliacaoModelAssembler.toModel(dto);
        return ResponseEntity.ok(model);
    }

    @GetMapping(produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-yml"})
    public ResponseEntity<CollectionModel<AvaliacaoModel>> findAll() {
        List<AvaliacaoResponseDto> avaliacaoList = avaliacaoService.findAll();
        List<AvaliacaoModel> models = avaliacaoList.stream()
                .map(avaliacaoModelAssembler::toModel)
                .toList();
        return ResponseEntity.ok(CollectionModel.of(models));
    }

    @PostMapping(value = "/create", produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-yml"},
            consumes = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-yml"})
    public ResponseEntity<AvaliacaoModel> create(@RequestBody AvaliacaoCreateDto avaliacaoCreateDto) {
        AvaliacaoResponseDto created = avaliacaoService.create(avaliacaoCreateDto);

        avaliacaoService.atualizarMediaAvaliacoes(created.getId());

        AvaliacaoModel model = avaliacaoModelAssembler.toModel(created);
        return ResponseEntity.ok(model);
    }

    @PutMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-yml"},
            consumes = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-yml"})
    public ResponseEntity<AvaliacaoModel> update(@PathVariable Long id, @RequestBody AvaliacaoCreateDto dto) {
        dto.setUserId(id);
        AvaliacaoCreateDto atualizado = avaliacaoService.update(dto);
        AvaliacaoResponseDto updatedDto = avaliacaoService.findById(id); // Para manter consistência com HATEOAS
        AvaliacaoModel model = avaliacaoModelAssembler.toModel(updatedDto);
        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @RequestHeader("X-User-Id") Long userId) {
        avaliacaoService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/usuario/{userId}/video/{videoId}")
    public ResponseEntity<AvaliacaoModel> findByUsuarioAndVideo(
            @PathVariable Long userId,
            @PathVariable Long videoId) {

        AvaliacaoResponseDto dto = avaliacaoService.findByUsuarioAndVideo(userId, videoId);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }

        AvaliacaoModel model = avaliacaoModelAssembler.toModel(dto);
        return ResponseEntity.ok(model);
    }
}
