package br.com.tiinforma.backend.domain.avaliacao;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Setter
public class AvaliacaoModel extends RepresentationModel<AvaliacaoModel> {

    private Long id;
    private Integer nota;
    private String comentario;
}
