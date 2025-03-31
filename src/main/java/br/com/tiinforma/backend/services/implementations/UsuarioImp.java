package br.com.tiinforma.backend.services.implementations;

import br.com.tiinforma.backend.domain.usuario.Usuario;
import br.com.tiinforma.backend.domain.usuario.UsuarioCreateDto;
import br.com.tiinforma.backend.domain.usuario.UsuarioResponseDto;
import br.com.tiinforma.backend.exceptions.ResourceNotFoundException;
import br.com.tiinforma.backend.mapper.DozerMapper;
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

    @Override
    public UsuarioResponseDto findById(Long id) {
        var usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado: " + id));
        return DozerMapper.parseObject(usuario, UsuarioResponseDto.class);
    }

    @Override
    public List<UsuarioResponseDto> findAll() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return DozerMapper.parseListObject(usuarios, UsuarioResponseDto.class);
    }

    @Override
    public UsuarioResponseDto create(UsuarioCreateDto usuarioCreateDto) {
        var entity = DozerMapper.parseObject(usuarioCreateDto, Usuario.class);
        entity = usuarioRepository.save(entity);
        return DozerMapper.parseObject(entity, UsuarioResponseDto.class);
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
        return DozerMapper.parseObject(usuarioAtualizado, UsuarioCreateDto.class);
    }

    @Override
    public void delete(Long id) {
        var usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado: " + id));
        usuarioRepository.delete(usuario);
    }
}
