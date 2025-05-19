package br.com.tiinforma.backend.domain.usuario;

import br.com.tiinforma.backend.controller.UsuarioController;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class UsuarioModelAssembler implements RepresentationModelAssembler<UsuarioResponseDto, UsuarioModel> {

    @Override
    public UsuarioModel toModel(UsuarioResponseDto entity) {
        UsuarioModel model = new UsuarioModel();
        model.setId(entity.getId());
        model.setNome(entity.getNome());
        model.setEmail(entity.getEmail());
        model.setInteresses(entity.getInteresses());
        model.setFuncao(entity.getFuncao());

        model.add(linkTo(methodOn(UsuarioController.class).findById(entity.getId())).withSelfRel());
        model.add(linkTo(methodOn(UsuarioController.class).findAll()).withRel("todos-usuarios"));
        model.add(
                linkTo(UsuarioController.class)
                        .slash(entity.getId())
                        .withRel("atualizar-usuario")
        );
        model.add(
                linkTo(UsuarioController.class)
                        .slash(entity.getId())
                        .withRel("deletar-usuario")
        );

        return model;
    }
}
