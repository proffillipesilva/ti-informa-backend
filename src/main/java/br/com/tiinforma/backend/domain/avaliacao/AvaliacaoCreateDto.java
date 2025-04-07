package br.com.tiinforma.backend.domain.avaliacao;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class AvaliacaoCreateDto {

    @NotNull(message = "O ID do usuário é obrigatório")
    private Long userId;

    @NotNull(message = "A nota é obrigatória")
    private Integer nota;

    @NotNull(message = "O comentário é obrigatório")
    private String comentario;

}
