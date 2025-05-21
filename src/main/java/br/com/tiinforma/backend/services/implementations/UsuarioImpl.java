package br.com.tiinforma.backend.services.implementations;

import br.com.tiinforma.backend.domain.enums.Funcao;
import br.com.tiinforma.backend.domain.usuario.administrador.AdministradorCreateDto;
import br.com.tiinforma.backend.domain.usuario.Usuario;
import br.com.tiinforma.backend.domain.usuario.UsuarioCreateDto;
import br.com.tiinforma.backend.domain.usuario.UsuarioResponseDto;
import br.com.tiinforma.backend.domain.usuario.administrador.AdministradorResponseDto;
import br.com.tiinforma.backend.exceptions.ResourceNotFoundException;
import br.com.tiinforma.backend.repositories.CriadorRepository;
import br.com.tiinforma.backend.repositories.UsuarioRepository;
import br.com.tiinforma.backend.services.interfaces.UsuarioService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CriadorRepository criadorRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public UsuarioResponseDto findById(Long id) {
        var usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + id));
        return modelMapper.map(usuario, UsuarioResponseDto.class);
    }

    @Override
    public List<UsuarioResponseDto> findAll() {
        return usuarioRepository.findAll()
                .stream()
                .map(usuario -> modelMapper.map(usuario, UsuarioResponseDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public UsuarioResponseDto create(UsuarioCreateDto usuarioCreateDto) {

        validarEmailUnico(usuarioCreateDto.getEmail());


        var entity = modelMapper.map(usuarioCreateDto, Usuario.class);
        entity = usuarioRepository.save(entity);
        return modelMapper.map(entity, UsuarioResponseDto.class);
    }

    @Override
    public UsuarioResponseDto update(Long id, UsuarioCreateDto usuarioCreateDto) {
        var usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        usuario.setNome(usuarioCreateDto.getNome());
        usuario.setEmail(usuarioCreateDto.getEmail());
        usuario.setSenha(usuarioCreateDto.getSenha());
        usuario.setInteresses(usuarioCreateDto.getInteresses());

        var usuarioAtualizado = usuarioRepository.save(usuario);
        return modelMapper.map(usuarioAtualizado, UsuarioResponseDto.class);
    }

    @Override
    public void delete(Long id) {
        var usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + id));
        usuarioRepository.delete(usuario);
    }

    @Override
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public AdministradorResponseDto criarAdministrador(AdministradorCreateDto administradorCreateDto) {

        administradorCreateDto.setFuncao(Funcao.ADMINISTRADOR);

        validarEmailUnico(administradorCreateDto.getEmail());


        Usuario usuario = Usuario.builder()
                .nome(administradorCreateDto.getNome())
                .email(administradorCreateDto.getEmail())
                .senha(administradorCreateDto.getSenha())
                .funcao(Funcao.ADMINISTRADOR)
                .fotoUrl(administradorCreateDto.getFotoUrl())
                .build();

        usuario = usuarioRepository.save(usuario);

        return modelMapper.map(usuario, AdministradorResponseDto.class);
    }

    private void validarEmailUnico(String email) {
        if (usuarioRepository.findByEmail(email).isPresent() ||
                criadorRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Já existe um usuário ou criador com este e-mail");
        }
    }


}
