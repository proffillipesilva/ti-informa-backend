package br.com.tiinforma.backend.domain.criador;

import br.com.tiinforma.backend.domain.enums.Funcao;
import br.com.tiinforma.backend.domain.playlist.Playlist;
import br.com.tiinforma.backend.domain.video.Video;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import br.com.tiinforma.backend.services.interfaces.FotoAtualizavel;
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
public class Criador implements Serializable, FotoAtualizavel {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_criador")
    private Long id;

    private String nome;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String cpf;

    private String senha;

    private String formacao;

    private Funcao funcao = Funcao.CRIADOR;

    private String fotoUrl;

    @OneToMany(mappedBy = "criador")
    @JsonManagedReference
    @Builder.Default
    private List<Playlist> playlists= new ArrayList<>();

    @OneToMany(mappedBy = "criador")
    @JsonManagedReference
    @Builder.Default
    private List<Video> videos= new ArrayList<>();

}
