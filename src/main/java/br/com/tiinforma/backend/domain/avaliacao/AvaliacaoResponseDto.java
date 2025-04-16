package br.com.tiinforma.backend.domain.avaliacao;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AvaliacaoResponseDto {
    private Long id;
    private Integer nota;
    private String comentario;
}
