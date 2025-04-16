package br.com.tiinforma.backend.services.implementations;

import br.com.tiinforma.backend.domain.assinatura.AssinaturaCreateDto;
import br.com.tiinforma.backend.domain.assinatura.AssinaturaResponseDto;
import br.com.tiinforma.backend.domain.enums.Plano;
import br.com.tiinforma.backend.exceptions.ResourceNotFoundException;
import br.com.tiinforma.backend.repositories.AssinaturaRepository;
import br.com.tiinforma.backend.services.interfaces.AssinaturaService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class AssinaturaImpl implements AssinaturaService {

    @Autowired
    private AssinaturaRepository assinaturaRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public AssinaturaResponseDto findById(Long id) {
        var assinatura = assinaturaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assinatura não encontrada: " + id));
        return modelMapper.map(assinatura, AssinaturaResponseDto.class);
    }

    @Override
    public List<AssinaturaResponseDto> findAll() {
        return assinaturaRepository.findAll()
                .stream()
                .map(assinatura -> modelMapper.map(assinatura, AssinaturaResponseDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public AssinaturaResponseDto create(AssinaturaCreateDto assinaturaCreateDto) {
        var assinatura = assinaturaRepository.findById(assinaturaCreateDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + assinaturaCreateDto.getUserId()));

        assinatura.setPlano(Plano.valueOf(assinaturaCreateDto.getPlano().toUpperCase()));
        assinatura.setDataInicio(assinaturaCreateDto.getDataInicio() != null ? assinaturaCreateDto.getDataInicio() : LocalDate.now());
        assinatura.setDataFim(assinaturaCreateDto.getDataFim());
        assinatura.setPreco(assinaturaCreateDto.getPreco());

        assinatura = assinaturaRepository.save(assinatura);
        return modelMapper.map(assinatura, AssinaturaResponseDto.class);
    }

    @Override
    public AssinaturaCreateDto update(AssinaturaCreateDto assinaturaCreateDto) {
        var assinatura = assinaturaRepository.findById(assinaturaCreateDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Assinatura não encontrada"));

        assinatura.setPlano(Plano.valueOf(assinaturaCreateDto.getPlano().toUpperCase()));
        assinatura.setDataInicio(assinaturaCreateDto.getDataInicio());
        assinatura.setDataFim(assinaturaCreateDto.getDataFim());
        assinatura.setPreco(assinaturaCreateDto.getPreco());

        var assinaturaAtualizada = assinaturaRepository.save(assinatura);
        return modelMapper.map(assinaturaAtualizada, AssinaturaCreateDto.class);
    }

    @Override
    public void delete(Long id) {
        var assinatura = assinaturaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assinatura não encontrada: " + id));
        assinaturaRepository.delete(assinatura);
    }
}
