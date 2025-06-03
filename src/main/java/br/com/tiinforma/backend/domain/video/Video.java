package br.com.tiinforma.backend.domain.video;

import br.com.tiinforma.backend.domain.criador.Criador;
import br.com.tiinforma.backend.domain.playlistVideo.PlaylistVideo;
import br.com.tiinforma.backend.domain.usuarioVideoProgresso.UsuarioVideoProgresso;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Video implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_video")
    private Long id;

    private String titulo;

    private String thumbnail;

    @Column(name = "video_key")
    private String key;

    private String descricao;

    private LocalDate dataPublicacao = LocalDate.now();

    private String categoria;

    @Column(name = "palavra_chave")
    private String palavraChave;

    private Long visualizacoes = 0L;

    @ManyToOne
    @JoinColumn(name = "id_criador")
    @JsonBackReference
    private Criador criador;

    @OneToMany(mappedBy = "video")
    @Builder.Default
    @JsonIgnore
    private List<PlaylistVideo> playlistVideos = new ArrayList<>();

    @OneToMany(mappedBy = "video")
    @Builder.Default
    @JsonIgnore
    private List<UsuarioVideoProgresso> progressos = new ArrayList<>();
}