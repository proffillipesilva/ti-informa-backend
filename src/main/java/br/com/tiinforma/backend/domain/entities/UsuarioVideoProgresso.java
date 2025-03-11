package br.com.tiinforma.backend.domain.entities;

import br.com.tiinforma.backend.domain.entities.embeddedPk.UsuarioVideoProgressoId;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UsuarioVideoProgresso {

    @EmbeddedId
    private UsuarioVideoProgressoId id;

    @ManyToOne
    @MapsId("usuarioId")
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne
    @MapsId("videoId")
    @JoinColumn(name = "id_video")
    private Video video;

    @ManyToOne
    @MapsId("avaliacaoId")
    @JoinColumn(name = "id_avaliacao")
    private Avaliacao avaliacao;

    private LocalDate dataInicio;
    private LocalDateTime tempoAssistido;

}
