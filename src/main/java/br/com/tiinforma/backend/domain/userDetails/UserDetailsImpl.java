package br.com.tiinforma.backend.domain.userDetails;

import br.com.tiinforma.backend.domain.criador.Criador;
import br.com.tiinforma.backend.domain.enums.Funcao;
import br.com.tiinforma.backend.domain.usuario.Usuario;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String senha;
    private List<Funcao> funcoes;

    public UserDetailsImpl(Usuario usuario) {
        this.id = usuario.getId_usuario();
        this.email = usuario.getEmail();
        this.senha = usuario.getSenha();
        this.funcoes = List.of(usuario.getFuncao());
    }

    public UserDetailsImpl(Criador criador) {
        this.id = criador.getId_criador();
        this.email = criador.getEmail();
        this.senha = criador.getSenha();
        this.funcoes = List.of(criador.getFuncao());
    }

    public UserDetailsImpl(Usuario usuario, Criador criador) {
        this.id = usuario != null ? usuario.getId_usuario() : criador.getId_criador();
        this.email = usuario != null ? usuario.getEmail() : criador.getEmail();
        this.senha = usuario != null ? usuario.getSenha() : criador.getSenha();
        this.funcoes = new ArrayList<>();
        if (usuario != null) {
            this.funcoes.add(usuario.getFuncao());
        }
        if (criador != null) {
            this.funcoes.add(criador.getFuncao());
        }

        this.funcoes = this.funcoes.stream().distinct().toList();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (Funcao funcao : this.funcoes) {
            authorities.add(new SimpleGrantedAuthority(funcao.name()));
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}