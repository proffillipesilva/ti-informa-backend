package br.com.tiinforma.backend.domain.criador;

import br.com.tiinforma.backend.domain.enums.Funcao;
import br.com.tiinforma.backend.domain.playlist.Playlist;
import br.com.tiinforma.backend.domain.video.Video;
import jakarta.persistence.*;
import lombok.*;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Criador implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_criador;

    private String nome;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String cpf;

    private String senha;

    private String formacao;

    private Funcao funcao = Funcao.CRIADOR;

    @OneToMany(mappedBy = "criador")
    @Builder.Default
    private List<Playlist> playlists= new ArrayList<>();

    @OneToMany(mappedBy = "criador")
    @Builder.Default
    private List<Video> videos= new ArrayList<>();

}
