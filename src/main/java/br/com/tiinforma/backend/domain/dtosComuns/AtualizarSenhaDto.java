package br.com.tiinforma.backend.domain.dtosComuns;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AtualizarSenhaDto(
        @NotBlank String senhaAntiga,
        @NotBlank @Size(min = 8) String novaSenha
) {}
