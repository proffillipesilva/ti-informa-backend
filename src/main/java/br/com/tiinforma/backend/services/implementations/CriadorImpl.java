package br.com.tiinforma.backend.services.implementations;

import br.com.tiinforma.backend.domain.criador.Criador;
import br.com.tiinforma.backend.domain.criador.CriadorCreateDto;
import br.com.tiinforma.backend.domain.criador.CriadorResponseDto;
import br.com.tiinforma.backend.domain.enums.Funcao;
import br.com.tiinforma.backend.domain.usuario.Usuario;
import br.com.tiinforma.backend.domain.usuario.UsuarioCreateDto;
import br.com.tiinforma.backend.exceptions.ResourceNotFoundException;
import br.com.tiinforma.backend.repositories.CriadorRepository;
import br.com.tiinforma.backend.repositories.UsuarioRepository;
import br.com.tiinforma.backend.services.interfaces.CriadorService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CriadorImpl implements CriadorService {

    @Autowired
    private CriadorRepository criadorRepository;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UsuarioRepository usuarioRepository;


    @Override
    public CriadorResponseDto findById(Long id) {
        var criador = criadorRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Criador não encontrad: " + id));
        return modelMapper.map(criador, CriadorResponseDto.class);
    }

    @Override
    public List<CriadorResponseDto> findAll() {
        return criadorRepository.findAll().stream()
                .map(criador -> modelMapper.map(criador, CriadorResponseDto.class))
                .collect(Collectors.toList());
    }
    
    @Override
    public CriadorResponseDto update(Long id, CriadorCreateDto criadorCreateDto) {
        var criador = criadorRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Criador não encontrad: " + id));

        UsuarioCreateDto usuarioCreateDto = modelMapper.map(criadorCreateDto, UsuarioCreateDto.class);
        criador.setNome(usuarioCreateDto.getNome());
        criador.setEmail(usuarioCreateDto.getEmail());
        criador.setCpf(criador.getCpf());
        criador.setSenha(usuarioCreateDto.getSenha());
        criador.setFormacao(criadorCreateDto.getFormacao());

        criadorRepository.save(criador);

        return modelMapper.map(criador, CriadorResponseDto.class);
    }

    @Override
    public void delete(Long id) {
        var criador = criadorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Criador não encontrado: " + id));
        criadorRepository.delete(criador);
    }

    @Override
    public CriadorResponseDto promoverParaCriador(
            Long id,
            String cpf,
            String formacao) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario não encontrado"));

        if (criadorRepository.findByEmail(usuario.getEmail()).isPresent()){
            throw new IllegalArgumentException("Já existe um criador com este e-mail");
        }

        if (criadorRepository.findByEmail(usuario.getEmail()).isPresent() ||
                usuarioRepository.findByEmail(usuario.getEmail())
                        .filter(u -> !u.getId().equals(usuario.getId()))
                        .isPresent()) {
            throw new IllegalArgumentException("Já existe um criador ou outro usuário com este e-mail");
        }



        if (criadorRepository.findByCpf(cpf).isPresent()) {
            throw new IllegalArgumentException("Já existe um criador com este cpf");
        }
        Criador criador = Criador.builder()
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .cpf(cpf)
                .senha(usuario.getSenha())
                .formacao(formacao)
                .funcao(Funcao.CRIADOR)
                .fotoUrl(usuario.getFotoUrl())
                .build();

        criadorRepository.save(criador);

        usuarioRepository.delete(usuario);


        return modelMapper.map(criador, CriadorResponseDto.class);
    }


}
