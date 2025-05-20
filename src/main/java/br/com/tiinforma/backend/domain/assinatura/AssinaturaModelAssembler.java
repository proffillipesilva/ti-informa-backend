package br.com.tiinforma.backend.domain.assinatura;

import br.com.tiinforma.backend.controller.AssinaturaController;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class AssinaturaModelAssembler implements RepresentationModelAssembler<AssinaturaResponseDto, AssinaturaModel> {

    @Override
    public AssinaturaModel toModel(AssinaturaResponseDto entity) {
        AssinaturaModel model = new AssinaturaModel();
        model.setId(entity.getId());
        model.setIdUsuario(entity.getIdUsuario());
        model.setPlano(entity.getPlano());
        model.setDataInicio(entity.getDataInicio());
        model.setDataFim(entity.getDataFim());
        model.setPreco(entity.getPreco());

        model.add(linkTo(methodOn(AssinaturaController.class).findById(entity.getId())).withSelfRel());
        model.add(linkTo(methodOn(AssinaturaController.class).findAll()).withRel("todas-assinaturas"));
        model.add(
                linkTo(AssinaturaController.class)
                        .slash(entity.getId())
                        .withRel("atualizar-assinatura")
        );
        model.add(
                linkTo(AssinaturaController.class)
                        .slash(entity.getId())
                        .withRel("deletar-assinatura")
        );

        return model;
    }
}