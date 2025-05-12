package br.com.tiinforma.backend.domain.usuario;

import br.com.tiinforma.backend.domain.enums.Funcao;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioResponseDto {
    private Long id_usuario;
    private String nome;
    private String email;
    private String interesses;
    private Funcao funcao;
}
