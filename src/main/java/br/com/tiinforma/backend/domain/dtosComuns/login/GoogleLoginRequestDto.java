package br.com.tiinforma.backend.domain.dtosComuns.login;

import br.com.tiinforma.backend.domain.usuario.UsuarioResponseDto;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GoogleLoginRequestDto {

    @NotBlank(message = "Firebase ID token cannot be empty")
    private String idToken;
    private String interesses;
    private List<UsuarioResponseDto> pergunta_resposta;

}
