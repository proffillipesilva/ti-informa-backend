package br.com.tiinforma.backend.domain.criador;

import br.com.tiinforma.backend.domain.playlist.Playlist;
import br.com.tiinforma.backend.domain.video.Video;
import jakarta.persistence.*;
import lombok.*;


import java.io.Serializable;
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
    private Long id;

    private String nome;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String cpf;

    @Column(unique = true)
    private String rg;

    private String senha;

    private String formacao;

    @OneToMany(mappedBy = "criador")
    private List<Playlist> playlists;

    @OneToMany(mappedBy = "criador")
    private List<Video> videos;

}
