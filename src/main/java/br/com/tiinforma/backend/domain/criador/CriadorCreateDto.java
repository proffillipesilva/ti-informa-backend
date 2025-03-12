package br.com.tiinforma.backend.domain.criador;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CriadorCreateDto(
        @NotBlank String nome,

        @Email String email,

        @Pattern(regexp = "\\d{11}")
        String cpf,

        @Pattern(regexp = "\\d{8,20}")
        String rg,

        @Size(min = 8, message = "A senha deve conter no minimo 8 caracteres")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!]).*$",
                message = "A senha deve ter pelo menos 1 uma letra maiuscula, 1 letra minuscula, 1 número , e 1 caractere especial"
        )
        String senha,

        String formacao

) {
}
