package br.com.tiinforma.backend.services.implementations;

import br.com.tiinforma.backend.domain.avaliacao.AvaliacaoCreateDto;
import br.com.tiinforma.backend.domain.avaliacao.AvaliacaoResponseDto;
import br.com.tiinforma.backend.exceptions.ResourceNotFoundException;
import br.com.tiinforma.backend.repositories.AvaliacaoRepository;
import br.com.tiinforma.backend.services.interfaces.AvaliacaoService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class AvaliacaoImpl implements AvaliacaoService {

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public AvaliacaoResponseDto findById(Long id) {
        var avaliacao = avaliacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Avaliação não encontrada: " + id));
        return modelMapper.map(avaliacao, AvaliacaoResponseDto.class);
    }

    @Override
    public List<AvaliacaoResponseDto> findAll() {
        return avaliacaoRepository.findAll()
                .stream()
                .map(avaliacao -> modelMapper.map(avaliacao, AvaliacaoResponseDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public AvaliacaoResponseDto create(AvaliacaoCreateDto avaliacaoCreateDto) {
        var avaliacao = avaliacaoRepository.findById(avaliacaoCreateDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + avaliacaoCreateDto.getUserId()));
        avaliacao.setNota(avaliacaoCreateDto.getNota());
        avaliacao.setComentario(avaliacaoCreateDto.getComentario());

        avaliacao = avaliacaoRepository.save(avaliacao);
        return modelMapper.map(avaliacao, AvaliacaoResponseDto.class);
    }

    @Override
    public AvaliacaoCreateDto update(AvaliacaoCreateDto avaliacaoCreateDto) {
        var avaliacao = avaliacaoRepository.findById(avaliacaoCreateDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Avaliação não encontrada"));

        avaliacao.setNota(avaliacaoCreateDto.getNota());
        avaliacao.setComentario(avaliacaoCreateDto.getComentario());

        var avaliacaoAtualizada = avaliacaoRepository.save(avaliacao);
        return modelMapper.map(avaliacaoAtualizada, AvaliacaoCreateDto.class);
    }

    @Override
    public void delete(Long id) {
        var avaliacao = avaliacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Avaliação não encontrada: " + id));
        avaliacaoRepository.delete(avaliacao);
    }
}
