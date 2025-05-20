package br.com.tiinforma.backend.domain.criador;

import br.com.tiinforma.backend.controller.CriadorController;

import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class CriadorModelAssembler implements RepresentationModelAssembler<CriadorResponseDto,CriadorModel > {

    @Override
    public CriadorModel toModel(CriadorResponseDto entity) {
        CriadorModel model = new CriadorModel();
        model.setId(entity.getId());
        model.setNome(entity.getNome());
        model.setEmail(entity.getEmail());
        model.setFormacao(entity.getFormacao());
        model.setFuncao(entity.getFuncao());

        model.add(linkTo(methodOn(CriadorController.class).findById(entity.getId())).withSelfRel());
        model.add(linkTo(methodOn(CriadorController.class).findAll()).withRel("todos-usuarios"));
        model.add(
                linkTo(CriadorController.class)
                        .slash(entity.getId())
                        .withRel("atualizar-usuario")
        );
        model.add(
                linkTo(CriadorController.class)
                        .slash(entity.getId())
                        .withRel("deletar-usuario")
        );

        return model;
    }
}
