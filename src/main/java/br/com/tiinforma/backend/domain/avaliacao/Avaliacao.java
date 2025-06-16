package br.com.tiinforma.backend.domain.avaliacao;

import br.com.tiinforma.backend.domain.UsuarioAvaliacao.UsuarioAvaliacao;
import br.com.tiinforma.backend.domain.usuario.Usuario;
import br.com.tiinforma.backend.domain.usuarioVideoProgresso.UsuarioVideoProgresso;
import br.com.tiinforma.backend.domain.video.Video;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id_usuario", "id_video"})
})
public class Avaliacao implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_avaliacao")
    private Long id;

    private Integer nota;
    private String comentario;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_video")
    private Video video;

    @OneToMany(mappedBy = "avaliacao")
    private List<UsuarioVideoProgresso> progressos;

    @OneToMany(mappedBy = "avaliacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UsuarioAvaliacao> usuarioAvaliacoes;
}