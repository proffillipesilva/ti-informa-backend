package br.com.tiinforma.backend.services.interfaces;


import br.com.tiinforma.backend.domain.criador.CriadorCreateDto;
import br.com.tiinforma.backend.domain.userDetails.UserDetailsImpl;
import br.com.tiinforma.backend.domain.usuario.administrador.AdministradorCreateDto;
import br.com.tiinforma.backend.domain.usuario.UsuarioCreateDto;
import br.com.tiinforma.backend.domain.usuario.UsuarioResponseDto;
import br.com.tiinforma.backend.domain.usuario.administrador.AdministradorResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Map;

public interface UsuarioService {

    UsuarioResponseDto findById(Long id);

    List<UsuarioResponseDto> findAll();

    UsuarioResponseDto create(UsuarioCreateDto usuarioCreateDto);

    UsuarioResponseDto update(Long id,UsuarioCreateDto usuarioCreateDto);

    boolean existeCriadorComCpf(String cpf);
    boolean existeSolicitacaoPendente(String email);

    void delete(Long id);

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    AdministradorResponseDto criarAdministrador(AdministradorCreateDto administradorCreateDto);

    ResponseEntity<?> solicitarCriador(CriadorCreateDto criadorDto, UserDetailsImpl userDetails);

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    ResponseEntity<List<Map<String, Object>>> getSolicitacoesCriador();

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    ResponseEntity<?> aprovarCriador(Long id);

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    ResponseEntity<?> reprovarCriador(Long id);

    void atualizarDescricao(Long id, String descricao);

}
