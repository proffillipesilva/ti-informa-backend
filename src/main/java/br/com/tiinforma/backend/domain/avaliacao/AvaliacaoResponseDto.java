package br.com.tiinforma.backend.domain.avaliacao;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AvaliacaoResponseDto {
    @Size(max = 5)
    private Long id;
    private Integer nota;
    private String comentario;
    private LocalDateTime dataInicio;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataAvaliacao;
}