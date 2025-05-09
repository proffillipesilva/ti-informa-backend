package br.com.tiinforma.backend.domain.usuario;

import br.com.tiinforma.backend.domain.assinatura.Assinatura;
import br.com.tiinforma.backend.domain.enums.Funcao;
import br.com.tiinforma.backend.domain.usuarioVideoProgresso.UsuarioVideoProgresso;
import br.com.tiinforma.backend.domain.playlist.Playlist;
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
public class Usuario implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;

    private String nome;

    @Column(unique = true)
    private String email;

    private String senha;

    private String interesses;

    private Funcao funcao = Funcao.USUARIO;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @Builder.Default
    private List<Playlist> playlists= new ArrayList<>();

    @OneToMany(mappedBy = "usuario")
    @Builder.Default
    private List<UsuarioVideoProgresso> progressos = new ArrayList<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @Builder.Default
    private List<Assinatura> assinaturas= new ArrayList<>();
}
