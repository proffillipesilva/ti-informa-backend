package br.com.tiinforma.backend.services.implementations;

import br.com.tiinforma.backend.domain.usuario.Usuario;
import br.com.tiinforma.backend.domain.usuario.UsuarioCreateDto;
import br.com.tiinforma.backend.domain.usuario.UsuarioResponseDto;
import br.com.tiinforma.backend.exceptions.ResourceNotFoundException;
import br.com.tiinforma.backend.mapper.UsuarioMapper;
import br.com.tiinforma.backend.repositories.UsuarioRepository;
import br.com.tiinforma.backend.services.interfaces.UsuarioService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class UsuarioImp implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private final UsuarioMapper usuarioMapper;

    @Override
    public UsuarioResponseDto findById(Long id) {
        var client = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado" + id));
        return UsuarioMapper.INSTANCE.toDto(client);
    }

    @Override
    public List<UsuarioResponseDto> findAll() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return UsuarioMapper.INSTANCE.toDtoList(usuarios);
    }

    @Override
    public UsuarioResponseDto create(UsuarioCreateDto usuarioCreateDto) {
        var entity = usuarioMapper.toEntity(usuarioCreateDto);
        entity = usuarioRepository.save(entity);
        return usuarioMapper.toDto(entity);
    }


    @Override
    @Transactional
    public UsuarioCreateDto update(UsuarioCreateDto usuarioCreateDto) {
        var usuario = usuarioRepository.findById(Long.valueOf(usuarioCreateDto.id()))
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado"));

        usuario.setNome(usuarioCreateDto.nome());
        usuario.setEmail(usuarioCreateDto.email());
        usuario.setPassword(usuarioCreateDto.password());
        usuario.setInteresses(usuarioCreateDto.interesses());
        usuario.setAssinaturas(usuario.getAssinaturas());
        usuario.setProgressos(usuario.getProgressos());

        var usuarioAtualizado = usuarioRepository.save(usuario);
        return usuarioMapper.toCreateDto(usuarioAtualizado);
    }

    @Override
    public void delete(Long id) {

    }
}
