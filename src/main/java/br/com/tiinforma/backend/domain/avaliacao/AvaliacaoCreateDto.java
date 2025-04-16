package br.com.tiinforma.backend.domain.avaliacao;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @Size(max = 10)
    private Integer nota;

    @NotNull(message = "O comentário é obrigatório")
    private String comentario;

}
