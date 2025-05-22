package br.com.tiinforma.backend.domain.playlist;

import br.com.tiinforma.backend.domain.enums.Visibilidade;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistCreateDto {
    private String nome;
    private Visibilidade visibilidade;
    private Long usuarioId;
    private Long criadorId;
}
