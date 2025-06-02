package br.com.tiinforma.backend.domain.avaliacao;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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

    @NotNull
    private Long videoId;

    @NotNull(message = "A nota é obrigatória")
    @Min(value = 1, message = "A nota mínima é 1")
    @Max(value = 5, message = "A nota máxima é 5")
    private Integer nota;

    @NotNull(message = "O comentário é obrigatório")
    private String comentario;

}