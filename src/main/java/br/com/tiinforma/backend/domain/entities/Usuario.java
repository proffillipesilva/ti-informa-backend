package br.com.tiinforma.backend.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

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
    private List<Playlist> playlists;

    @OneToMany(mappedBy = "usuario")
    private List<UsuarioVideoProgresso> progressos;

    @OneToMany(mappedBy = "usuario")
    private List<Assinatura> assinaturas;
}
