package br.com.tiinforma.backend.domain.assinatura;

import br.com.tiinforma.backend.domain.enums.Plano;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AssinaturaDto {
    private Long id;
    private Long idUsuario;
    private Plano plano;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private Double preco;
}
