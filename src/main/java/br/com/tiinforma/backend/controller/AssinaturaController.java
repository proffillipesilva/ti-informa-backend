package br.com.tiinforma.backend.controller;

import br.com.tiinforma.backend.domain.assinatura.AssinaturaCreateDto;
import br.com.tiinforma.backend.domain.assinatura.AssinaturaModel;
import br.com.tiinforma.backend.domain.assinatura.AssinaturaModelAssembler;
import br.com.tiinforma.backend.domain.assinatura.AssinaturaResponseDto;
import br.com.tiinforma.backend.services.interfaces.AssinaturaService;
import br.com.tiinforma.backend.util.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/assinaturas")
public class AssinaturaController {

    @Autowired
    private AssinaturaService assinaturaService;

    @Autowired
    private AssinaturaModelAssembler assinaturaModelAssembler;

    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-yml"})
    public ResponseEntity<AssinaturaModel> findById(@PathVariable Long id) {
        AssinaturaResponseDto dto = assinaturaService.findById(id);
        AssinaturaModel model = assinaturaModelAssembler.toModel(dto);
        return ResponseEntity.ok(model);
    }

    @GetMapping(produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-yml"})
    public ResponseEntity<CollectionModel<AssinaturaModel>> findAll() {
        List<AssinaturaResponseDto> dtos = assinaturaService.findAll();
        List<AssinaturaModel> models = dtos.stream()
                .map(assinaturaModelAssembler::toModel)
                .toList();
        return ResponseEntity.ok(CollectionModel.of(models));
    }

    @PostMapping(value = "/register", produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-yml"}, consumes = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-yml"})
    public ResponseEntity<AssinaturaModel> create(@RequestBody AssinaturaCreateDto dto) {
        AssinaturaResponseDto created = assinaturaService.create(dto);
        AssinaturaModel model = assinaturaModelAssembler.toModel(created);
        return ResponseEntity.status(201).body(model);
    }

    @PutMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-yml"}, consumes = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-yml"})
    public ResponseEntity<AssinaturaModel> update(@PathVariable Long id, @RequestBody AssinaturaCreateDto dto) {
        AssinaturaResponseDto updated = assinaturaService.update(id, dto);
        AssinaturaModel model = assinaturaModelAssembler.toModel(updated);
        return ResponseEntity.ok(model);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        assinaturaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
