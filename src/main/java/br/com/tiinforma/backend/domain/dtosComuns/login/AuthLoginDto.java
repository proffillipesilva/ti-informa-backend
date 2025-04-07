package br.com.tiinforma.backend.domain.dtosComuns.login;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthLoginDto {
    private String email;
    private String senha;
}
