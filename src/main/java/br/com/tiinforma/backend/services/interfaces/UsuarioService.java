package br.com.tiinforma.backend.services.interfaces;


import br.com.tiinforma.backend.domain.usuario.administrador.AdministradorCreateDto;
import br.com.tiinforma.backend.domain.usuario.UsuarioCreateDto;
import br.com.tiinforma.backend.domain.usuario.UsuarioResponseDto;
import br.com.tiinforma.backend.domain.usuario.administrador.AdministradorResponseDto;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface UsuarioService {

    UsuarioResponseDto findById(Long id);

    List<UsuarioResponseDto> findAll();

    UsuarioResponseDto create(UsuarioCreateDto usuarioCreateDto);

    UsuarioResponseDto update(Long id,UsuarioCreateDto usuarioCreateDto);

    void delete(Long id);

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    AdministradorResponseDto criarAdministrador(AdministradorCreateDto administradorCreateDto);
}
