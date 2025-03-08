package br.com.tiinforma.backend.domain.entities;

import br.com.tiinforma.backend.domain.enums.Plano;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Assinatura implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Plano plano;

    private LocalDate dataInicio = LocalDate.now();

    private LocalDate dataFim;

    private Double preco;
}
