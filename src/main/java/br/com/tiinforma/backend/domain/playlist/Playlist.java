package br.com.tiinforma.backend.domain.playlist;

import br.com.tiinforma.backend.domain.criador.Criador;
import br.com.tiinforma.backend.domain.playlistVideo.PlaylistVideo;
import br.com.tiinforma.backend.domain.usuario.Usuario;
import br.com.tiinforma.backend.domain.enums.Visibilidade;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Playlist implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_playlist;

    private String nome;

    @Enumerated(EnumType.STRING)
    private Visibilidade visibilidade;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_criador")
    private Criador criador;

    @OneToMany(mappedBy = "playlist")
    @Builder.Default
    private List<PlaylistVideo> playlistVideos= new ArrayList<>();
}
