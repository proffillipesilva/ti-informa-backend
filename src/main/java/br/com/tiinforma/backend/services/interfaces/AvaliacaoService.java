package br.com.tiinforma.backend.services.interfaces;

import br.com.tiinforma.backend.domain.avaliacao.AvaliacaoCreateDto;
import br.com.tiinforma.backend.domain.avaliacao.AvaliacaoResponseDto;

import java.util.List;

public interface AvaliacaoService {
    AvaliacaoResponseDto findById(Long id);

    AvaliacaoResponseDto findByUsuarioAndVideo(Long usuarioId, Long videoId);

    List<AvaliacaoResponseDto> findAll();

    AvaliacaoResponseDto create(AvaliacaoCreateDto avaliacaoCreateDto);

    AvaliacaoCreateDto update(AvaliacaoCreateDto avaliacaoCreateDto);

    void delete(Long id);

}