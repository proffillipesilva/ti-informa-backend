package br.com.tiinforma.backend.controller;

import br.com.tiinforma.backend.domain.assinatura.AssinaturaCreateDto;
import br.com.tiinforma.backend.domain.assinatura.AssinaturaDto;
import br.com.tiinforma.backend.exceptions.ResourceNotFoundException;
import br.com.tiinforma.backend.services.implementations.AssinaturaImpl;
import br.com.tiinforma.backend.util.MediaType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/assinaturas")
@CrossOrigin(origins = "http://localhost:8080")
@RequiredArgsConstructor
public class AssinaturaController {

    private final AssinaturaImpl assinaturaService;

    @GetMapping(
            value = "/{id}",
            produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-yml"}
    )
    public ResponseEntity<AssinaturaDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(assinaturaService.findById(id));
    }

    @GetMapping(
            produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-yml"}
    )
    public ResponseEntity<List<AssinaturaDto>> findAll() {
        return ResponseEntity.ok(assinaturaService.findAll());
    }

    @PostMapping(
            produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-yml"},
            consumes = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-yml"}
    )
    public ResponseEntity<AssinaturaDto> create(@RequestBody AssinaturaCreateDto assinaturaCreateDto) {
        AssinaturaDto created = assinaturaService.create(assinaturaCreateDto);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping(
            value = "/{id}",
            produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-yml"},
            consumes = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-yml"}
    )
    public ResponseEntity<AssinaturaCreateDto> update(@PathVariable Long id, @RequestBody AssinaturaCreateDto assinaturaCreateDto) {
        assinaturaCreateDto.setUserId(id);
        AssinaturaCreateDto updated = assinaturaService.update(assinaturaCreateDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping(
            value = "/{id}",
            produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-yml"}
    )
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        assinaturaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
