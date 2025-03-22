package br.com.tiinforma.backend.mapper;

import br.com.tiinforma.backend.domain.usuario.Usuario;
import br.com.tiinforma.backend.domain.usuario.UsuarioCreateDto;
import br.com.tiinforma.backend.domain.usuario.UsuarioResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    UsuarioMapper INSTANCE = Mappers.getMapper(UsuarioMapper.class);

    UsuarioResponseDto toDto(Usuario usuario);
    Usuario toEntity(UsuarioCreateDto dto);
    List<UsuarioResponseDto> toDtoList(List<Usuario> usuarios);
    UsuarioCreateDto toCreateDto(Usuario usuario);
}
