package br.com.tiinforma.backend.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Usuario implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Column(unique = true)
    private String email;

    private String password;

    private String interesses;

    @OneToMany(mappedBy = "usuario")
    private Set<Assinatura> assinaturas = new HashSet<>();

    @OneToMany(mappedBy = "usuario")
    private Set<Playlist> playlists = new HashSet<>();


    Set<Avaliacao> avaliacao = new HashSet<>();
}
