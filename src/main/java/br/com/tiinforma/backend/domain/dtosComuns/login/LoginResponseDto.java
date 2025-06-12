package br.com.tiinforma.backend.domain.dtosComuns.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {
    private String token;
    private String funcao;
    private boolean cadastroCompleto;

    public LoginResponseDto(String token) {
        this.token = token;
        this.funcao = "USUARIO";
        this.cadastroCompleto = false;
    }

}