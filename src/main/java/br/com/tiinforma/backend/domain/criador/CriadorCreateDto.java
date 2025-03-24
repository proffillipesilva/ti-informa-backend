package br.com.tiinforma.backend.domain.criador;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CriadorCreateDto {

        @NotBlank
        private String nome;

        @Email
        private String email;

        @Pattern(regexp = "\\d{11}")
        private String cpf;

        @Pattern(regexp = "\\d{8,20}")
        private String rg;

        @Size(min = 8, message = "A senha deve conter no mínimo 8 caracteres")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!]).*$",
                message = "A senha deve ter pelo menos 1 letra maiúscula, 1 letra minúscula, 1 número e 1 caractere especial"
        )
        private String senha;

        private String formacao;
}
