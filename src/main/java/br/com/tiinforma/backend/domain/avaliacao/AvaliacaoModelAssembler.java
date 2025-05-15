package br.com.tiinforma.backend.domain.avaliacao;

import br.com.tiinforma.backend.controller.AvaliacaoController;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class AvaliacaoModelAssembler implements RepresentationModelAssembler<AvaliacaoResponseDto, AvaliacaoModel> {

    @Override
    public AvaliacaoModel toModel(AvaliacaoResponseDto entity) {
        AvaliacaoModel model = new AvaliacaoModel();
        model.setId(entity.getId());
        model.setNota(entity.getNota());
        model.setComentario(entity.getComentario());

        model.add(linkTo(methodOn(AvaliacaoController.class).findById(entity.getId())).withSelfRel());
        model.add(linkTo(methodOn(AvaliacaoController.class).findAll()).withRel("todas-avaliacoes"));
        model.add(linkTo(AvaliacaoController.class).slash(entity.getId()).withRel("atualizar-avaliacao"));
        model.add(linkTo(AvaliacaoController.class).slash(entity.getId()).withRel("deletar-avaliacao"));

        return model;
    }
}
