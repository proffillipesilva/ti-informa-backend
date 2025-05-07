package br.com.tiinforma.backend.services.implementations;

import br.com.tiinforma.backend.domain.criador.Criador;
import br.com.tiinforma.backend.domain.criador.CriadorCreateDto;
import br.com.tiinforma.backend.domain.criador.CriadorResponseDto;
import br.com.tiinforma.backend.exceptions.ResourceNotFoundException;
import br.com.tiinforma.backend.repositories.CriadorRepository;
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
    public CriadorResponseDto create(CriadorCreateDto criadorCreateDto) {
        var entity = modelMapper.map(criadorCreateDto, Criador.class);
        entity = criadorRepository.save(entity);
        return modelMapper.map(entity, CriadorResponseDto.class);
    }

    @Override
    public CriadorResponseDto update(Long id, CriadorCreateDto criadorCreateDto) {
        var criador = criadorRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Criador não encontrad: " + id));

        criador.setNome(criadorCreateDto.getNome());
        criador.setEmail(criadorCreateDto.getEmail());
        criador.setCpf(criador.getCpf());
        criador.setSenha(criadorCreateDto.getSenha());
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
}
