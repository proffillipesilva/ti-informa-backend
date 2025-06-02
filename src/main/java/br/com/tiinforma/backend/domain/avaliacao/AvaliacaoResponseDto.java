package br.com.tiinforma.backend.domain.avaliacao;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AvaliacaoResponseDto {
    @Size(max = 5)
    private Long id;
    private Integer nota;
    private String comentario;
}
