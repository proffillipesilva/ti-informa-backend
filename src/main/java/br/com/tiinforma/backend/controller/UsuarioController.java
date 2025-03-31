package br.com.tiinforma.backend.controller;

import br.com.tiinforma.backend.domain.usuario.UsuarioCreateDto;
import br.com.tiinforma.backend.domain.usuario.UsuarioResponseDto;
import br.com.tiinforma.backend.services.interfaces.UsuarioService;
import br.com.tiinforma.backend.util.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuario")
@CrossOrigin(origins = "http://localhost:8080")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML,"application/x-yml"})
    public ResponseEntity<UsuarioResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.findById(id));
    }

    @GetMapping(produces = {MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML,"application/x-yml"})
    public ResponseEntity<List<UsuarioResponseDto>> findAll() {
        return ResponseEntity.ok(usuarioService.findAll());
    }

    @PostMapping(value ="/register",produces = {MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML,"application/x-yml"},consumes = {MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML,"application/x-yml"})
    public UsuarioResponseDto create(@RequestBody UsuarioCreateDto usuarioCreateDto) {
        return usuarioService.create(usuarioCreateDto);
    }

    @PutMapping(value = "/{id}",produces = {MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML,"application/x-yml"},consumes = {MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML,"application/x-yml"})
    public ResponseEntity<UsuarioCreateDto> update(@PathVariable Long id, @RequestBody UsuarioCreateDto dto) {
        dto.setId(id);
        UsuarioCreateDto atualizado = usuarioService.update(dto);
        return ResponseEntity.ok(atualizado);
    }

}
