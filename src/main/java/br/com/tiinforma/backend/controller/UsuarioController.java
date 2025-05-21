package br.com.tiinforma.backend.controller;

import br.com.tiinforma.backend.domain.usuario.UsuarioCreateDto;
import br.com.tiinforma.backend.domain.usuario.UsuarioModel;
import br.com.tiinforma.backend.domain.usuario.UsuarioModelAssembler;
import br.com.tiinforma.backend.domain.usuario.UsuarioResponseDto;
import br.com.tiinforma.backend.domain.usuario.administrador.AdministradorCreateDto;
import br.com.tiinforma.backend.domain.usuario.administrador.AdministradorResponseDto;
import br.com.tiinforma.backend.services.interfaces.UsuarioService;
import br.com.tiinforma.backend.util.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/usuario")
@CrossOrigin(origins = "http://localhost:8080")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioModelAssembler usuarioModelAssembler;

    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML,"application/x-yml"})
    public ResponseEntity<UsuarioModel> findById(@PathVariable Long id) {
        UsuarioResponseDto usuarioResponseDto = usuarioService.findById(id);
        UsuarioModel model = usuarioModelAssembler.toModel(usuarioResponseDto);
        return ResponseEntity.ok(model);
    }

    @GetMapping(produces = {MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML,"application/x-yml"})
    public ResponseEntity<CollectionModel<UsuarioModel>> findAll() {
        List<UsuarioResponseDto> usuarios = usuarioService.findAll();
        List<UsuarioModel> usuarioModels = usuarios.stream()
                .map(usuarioModelAssembler::toModel)
                .toList();
        return ResponseEntity.ok(CollectionModel.of(usuarioModels));
    }

    @PostMapping(value ="/register",produces = {MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML,"application/x-yml"},consumes = {MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML,"application/x-yml"})
    public ResponseEntity<UsuarioModel> create(@RequestBody UsuarioCreateDto usuarioCreateDto) {
        UsuarioResponseDto usuarioResponseDto = usuarioService.create(usuarioCreateDto);
        UsuarioModel model = usuarioModelAssembler.toModel(usuarioResponseDto);
        return ResponseEntity.created(model.getRequiredLink("self").toUri()).body(model);
    }

    @PutMapping(value = "/{id}",produces = {MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML,"application/x-yml"},consumes = {MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML,"application/x-yml"})
    public ResponseEntity<UsuarioModel> update(@PathVariable Long id,@RequestBody UsuarioCreateDto dto) {
        UsuarioResponseDto usuarioResponseDto = usuarioService.update(id, dto);
        UsuarioModel model = usuarioModelAssembler.toModel(usuarioResponseDto);
        return ResponseEntity.ok(model);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "registrar/administrador")
    public ResponseEntity<AdministradorResponseDto> criarAdministrador(@RequestBody AdministradorCreateDto administradorCreateDto) {
        AdministradorResponseDto administradorResponseDto = usuarioService.criarAdministrador(administradorCreateDto);
        return ResponseEntity.ok(administradorResponseDto);
    }

}
