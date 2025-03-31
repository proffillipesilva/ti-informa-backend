package br.com.tiinforma.backend.domain.assinatura;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
@AllArgsConstructor

public class AssinaturaCreateDto {
    @NotNull(message = "O ID do usuário é obrigatório")
    private Long userId;

    @NotNull(message = "O plano é obrigatório")
    private String plano;

    @NotNull(message = "A data de início é obrigatória")
    private LocalDate dataInicio;

    @Future(message = "A data de término deve estar no futuro")
    private LocalDate dataFim;

    @Positive(message = "O preço deve ser positivo")
    private Double preco;
}
