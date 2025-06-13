package br.com.tiinforma.backend.controller;

import br.com.tiinforma.backend.domain.criador.CriadorCreateDto;
import br.com.tiinforma.backend.domain.enums.Funcao;
import br.com.tiinforma.backend.domain.userDetails.UserDetailsImpl;
import br.com.tiinforma.backend.domain.usuario.UsuarioCreateDto;
import br.com.tiinforma.backend.domain.usuario.UsuarioModel;
import br.com.tiinforma.backend.domain.usuario.UsuarioModelAssembler;
import br.com.tiinforma.backend.domain.usuario.UsuarioResponseDto;
import br.com.tiinforma.backend.domain.usuario.administrador.AdministradorCreateDto;
import br.com.tiinforma.backend.domain.usuario.administrador.AdministradorResponseDto;
import br.com.tiinforma.backend.services.interfaces.UsuarioService;
import br.com.tiinforma.backend.util.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.CollectionModel;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/usuario")
@CrossOrigin(origins = "http://localhost:8080")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioModelAssembler usuarioModelAssembler;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<AdministradorResponseDto> criarAdministrador(@RequestBody AdministradorCreateDto administradorCreateDto) {
        AdministradorResponseDto administradorResponseDto = usuarioService.criarAdministrador(administradorCreateDto);
        return ResponseEntity.ok(administradorResponseDto);
    }

    @PostMapping("/solicitar-criador")
    public ResponseEntity<?> solicitarCriador(@RequestBody CriadorCreateDto criadorDto,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            if (criadorDto.getSenha() == null || criadorDto.getSenha().isEmpty()) {
                return ResponseEntity.badRequest().body("A senha é obrigatória");
            }
            if (criadorDto.getNome() == null || criadorDto.getNome().isEmpty()) {
                return ResponseEntity.badRequest().body("O nome é obrigatório");
            }
            if (criadorDto.getCpf() == null || criadorDto.getCpf().isEmpty()) {
                return ResponseEntity.badRequest().body("O CPF é obrigatório");
            }
            if (criadorDto.getFormacao() == null || criadorDto.getFormacao().isEmpty()) {
                return ResponseEntity.badRequest().body("A formação é obrigatória");
            }

            if (!criadorDto.getCpf().matches("\\d{11}")) {
                return ResponseEntity.badRequest().body("CPF deve conter exatamente 11 dígitos numéricos");
            }

            if (!passwordEncoder.matches(criadorDto.getSenha(), userDetails.getPassword())) {
                return ResponseEntity.badRequest().body("A senha fornecida não corresponde à sua senha atual");
            }

            if (userDetails.getFuncao() == Funcao.CRIADOR || userDetails.getFuncao() == Funcao.ADMINISTRADOR) {
                return ResponseEntity.badRequest().body("Você já possui permissões de criador");
            }

            if (usuarioService.existeCriadorComCpf(criadorDto.getCpf())) {
                return ResponseEntity.badRequest().body("Este CPF já está cadastrado no sistema");
            }

            if (usuarioService.existeSolicitacaoPendente(userDetails.getUsername())) {
                return ResponseEntity.badRequest().body("Já existe uma solicitação pendente para este e-mail");
            }

            return usuarioService.solicitarCriador(criadorDto, userDetails);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao processar solicitação: " + e.getMessage());
        }
    }

    @GetMapping("/solicitacoes-criador")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<Map<String, Object>>> getSolicitacoesCriador() {
        return usuarioService.getSolicitacoesCriador();
    }

    @PostMapping("/aprovar-criador/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> aprovarCriador(@PathVariable Long id) {
        return usuarioService.aprovarCriador(id);
    }

    @PostMapping("/reprovar-criador/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> reprovarCriador(@PathVariable Long id) {
        return usuarioService.reprovarCriador(id);
    }
}