package br.com.tiinforma.backend.domain.embeddedPk;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UsuarioVideoProgressoId implements Serializable {
    private Long usuarioId;
    private Long videoId;
    private Long avaliacaoId;

}
