package br.com.tiinforma.backend.controller;


import br.com.tiinforma.backend.domain.assinatura.AssinaturaCreateDto;
import br.com.tiinforma.backend.domain.assinatura.AssinaturaDto;

import br.com.tiinforma.backend.exceptions.ResourceNotFoundException;
import br.com.tiinforma.backend.services.implementations.AssinaturaImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/assinaturas")
@RequiredArgsConstructor
public class AssinaturaController {

    private final AssinaturaImpl assinaturaService;

    @GetMapping("/{id}")
    public ResponseEntity<AssinaturaDto> getAssinaturaById(@PathVariable Long id) {
        try {
            AssinaturaDto assinaturaDto = assinaturaService.findById(id);
            return ResponseEntity.ok(assinaturaDto);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping
    public ResponseEntity<List<AssinaturaDto>> getAllAssinaturas() {
        List<AssinaturaDto> assinaturas = assinaturaService.findAll();
        return ResponseEntity.ok(assinaturas);
    }

    @PostMapping
    public ResponseEntity<AssinaturaDto> createAssinatura(@RequestBody AssinaturaCreateDto assinaturaCreateDto) {
        AssinaturaDto assinaturaDto = assinaturaService.create(assinaturaCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(assinaturaDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AssinaturaCreateDto> updateAssinatura(@PathVariable Long id, @RequestBody AssinaturaCreateDto assinaturaCreateDto) {
        assinaturaCreateDto.setUserId(id);
        try {
            AssinaturaCreateDto updatedAssinatura = assinaturaService.update(assinaturaCreateDto);
            return ResponseEntity.ok(updatedAssinatura);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAssinatura(@PathVariable Long id) {
        try {
            assinaturaService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
