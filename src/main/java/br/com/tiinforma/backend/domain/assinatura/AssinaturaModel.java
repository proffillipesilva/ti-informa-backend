package br.com.tiinforma.backend.domain.assinatura;

import br.com.tiinforma.backend.domain.enums.Plano;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;

@Getter
@Setter
public class AssinaturaModel extends RepresentationModel<AssinaturaModel> {

    private Long id;
    private Long idUsuario;
    private Plano plano;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private Double preco;
}
