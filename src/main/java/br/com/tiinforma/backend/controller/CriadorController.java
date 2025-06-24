package br.com.tiinforma.backend.controller;

import br.com.tiinforma.backend.domain.criador.CriadorCreateDto;
import br.com.tiinforma.backend.domain.criador.CriadorResponseDto;
import br.com.tiinforma.backend.domain.inscricao.InscricaoRequestDto;
import br.com.tiinforma.backend.services.interfaces.CriadorService;
import br.com.tiinforma.backend.util.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/criador")
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


    @PutMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-yml"}, consumes = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-yml"})
    public ResponseEntity<CriadorResponseDto> update(@PathVariable Long id, @RequestBody CriadorCreateDto dto) {
        CriadorResponseDto criadorAtualizado = criadorService.update(id, dto);
        return ResponseEntity.ok(criadorAtualizado);
    }

    @DeleteMapping(value = "/{id}")
    public void delete(@PathVariable Long id) {
        criadorService.delete(id);
    }

    @PostMapping("/{id}/promover")
    public ResponseEntity<?> promoverUsuarioParaCriador(
            @PathVariable Long id,
            @RequestParam String cpf,
            @RequestParam String formacao) {
        try {
            var criadorDto = criadorService.promoverParaCriador(id, cpf, formacao);
            return ResponseEntity.ok(criadorDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/inscricao")
    public ResponseEntity<CriadorResponseDto> gerenciarInscricao(@RequestBody InscricaoRequestDto request) {
        CriadorResponseDto criador = criadorService.gerenciarInscricao(request);
        return ResponseEntity.ok(criador);
    }

        @GetMapping("/{id}/inscritos")
        public ResponseEntity<Integer> getTotalInscritos(@PathVariable Long id) {
            Integer totalInscritos = criadorService.getTotalInscritos(id);
            return ResponseEntity.ok(totalInscritos);
        }
    }


