package br.com.tiinforma.backend.domain.usuario.administrador;

import br.com.tiinforma.backend.domain.enums.Funcao;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdministradorResponseDto {

    private String nome;
    private String email;
    private Funcao funcao;
    private String fotoUrl;
}
