package br.com.tiinforma.backend.controller;

import br.com.tiinforma.backend.domain.criador.CriadorCreateDto;
import br.com.tiinforma.backend.domain.criador.CriadorModel;
import br.com.tiinforma.backend.domain.criador.CriadorModelAssembler;
import br.com.tiinforma.backend.domain.criador.CriadorResponseDto;
import br.com.tiinforma.backend.domain.usuario.UsuarioModel;
import br.com.tiinforma.backend.domain.usuario.UsuarioModelAssembler;
import br.com.tiinforma.backend.services.interfaces.CriadorService;
import br.com.tiinforma.backend.util.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/criador")
@CrossOrigin(origins = "http://localhost:8080")
public class CriadorController {

    @Autowired
    private CriadorService criadorService;

    @Autowired
    private CriadorModelAssembler criadorModel;



    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-yml"})
    public ResponseEntity<CriadorModel> findById(@PathVariable Long id) {
        CriadorResponseDto criadorResponseDto = criadorService.findById(id);
        CriadorModel model = criadorModel.toModel(criadorResponseDto);
        return ResponseEntity.ok(model);
    }

    @GetMapping(produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-yml"})
    public ResponseEntity<CollectionModel<CriadorModel>> findAll() {
       List<CriadorResponseDto> criadorResponseDto = criadorService.findAll();
       List<CriadorModel> model = criadorResponseDto.stream()
               .map(criadorModel::toModel)
               .toList();
       return ResponseEntity.ok(CollectionModel.of(model));

    }

    @PostMapping(value = "/register", produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-yml"}, consumes = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-yml"})
    public ResponseEntity<CriadorModel> create(@RequestBody CriadorCreateDto criadorCreateDto) {
        CriadorResponseDto criadorResponseDto = criadorService.create(criadorCreateDto);
        CriadorModel model = criadorModel.toModel(criadorResponseDto);
        return ResponseEntity.ok(model);
    }

    @PutMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-yml"}, consumes = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-yml"})
    public ResponseEntity<CriadorModel> update(@PathVariable Long id, @RequestBody CriadorCreateDto dto) {
        CriadorResponseDto criadorAtualizado = criadorService.update(id, dto);
        CriadorModel model = criadorModel.toModel(criadorAtualizado);
        return ResponseEntity.ok(model);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        criadorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
