package br.com.tiinforma.backend.services.interfaces;

import br.com.tiinforma.backend.domain.avaliacao.AvaliacaoCreateDto;
import br.com.tiinforma.backend.domain.avaliacao.AvaliacaoDto;

import java.util.List;

public interface AvaliacaoService {
    AvaliacaoDto findById(Long id);

    List<AvaliacaoDto> findAll();

    AvaliacaoDto create(AvaliacaoCreateDto avaliacaoCreateDto);

    AvaliacaoCreateDto update(AvaliacaoCreateDto avaliacaoCreateDto);

    void delete(Long id);
}
