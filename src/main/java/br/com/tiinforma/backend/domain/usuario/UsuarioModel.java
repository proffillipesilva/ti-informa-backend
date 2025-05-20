package br.com.tiinforma.backend.domain.usuario;

import br.com.tiinforma.backend.domain.enums.Funcao;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;


@Getter
@Setter
public class UsuarioModel extends RepresentationModel<UsuarioModel> {

    private Long id;
    private String nome;
    private String email;
    private String interesses;
    private Funcao funcao;

}
