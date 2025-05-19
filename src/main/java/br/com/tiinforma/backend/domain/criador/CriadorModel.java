package br.com.tiinforma.backend.domain.criador;

import br.com.tiinforma.backend.domain.enums.Funcao;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Setter
public class CriadorModel extends RepresentationModel<CriadorModel> {

    private Long id;
    private String nome;
    private String email;
    private String formacao;
    private Funcao funcao;

}
