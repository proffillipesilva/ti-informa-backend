package br.com.tiinforma.backend.domain.dtosComuns;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AtualizarSenhaDto {
    @NotBlank
    private String senhaAntiga;

    @NotBlank
    @Size(min = 8)
    private String novaSenha;
}
