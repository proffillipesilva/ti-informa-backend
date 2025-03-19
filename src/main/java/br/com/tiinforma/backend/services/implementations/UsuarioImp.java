package br.com.tiinforma.backend.services.implementations;

import br.com.tiinforma.backend.domain.usuario.UsuarioCreateDto;
import br.com.tiinforma.backend.domain.usuario.UsuarioResponseDto;
import br.com.tiinforma.backend.exceptions.ResourceNotFoundException;
import br.com.tiinforma.backend.mapper.DozerMapper;
import br.com.tiinforma.backend.repositories.UsuarioRepository;
import br.com.tiinforma.backend.services.interfaces.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioImp implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;


    @Override
    public UsuarioResponseDto findById(Long id) {
        var client = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado" + id));
        return DozerMapper.parseObject(client, UsuarioResponseDto.class);
    }

    @Override
    public List<UsuarioResponseDto> findAll() {
        return List.of();
    }

    @Override
    public UsuarioCreateDto save(UsuarioCreateDto usuarioCreateDto) {
        return null;
    }

    @Override
    public UsuarioCreateDto update(UsuarioCreateDto usuarioCreateDto) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }
}
