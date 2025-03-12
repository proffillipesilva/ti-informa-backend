package br.com.tiinforma.backend.domain.usuarioVideoProgresso;

import br.com.tiinforma.backend.domain.avaliacao.Avaliacao;
import br.com.tiinforma.backend.domain.embeddedPk.UsuarioVideoProgressoId;
import br.com.tiinforma.backend.domain.usuario.Usuario;
import br.com.tiinforma.backend.domain.video.Video;
import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
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
    private Duration tempoAssistido;

}
