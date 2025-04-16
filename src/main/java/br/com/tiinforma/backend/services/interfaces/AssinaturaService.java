package br.com.tiinforma.backend.services.interfaces;

import br.com.tiinforma.backend.domain.assinatura.AssinaturaCreateDto;
import br.com.tiinforma.backend.domain.assinatura.AssinaturaResponseDto;

import java.util.List;

public interface AssinaturaService {
    AssinaturaResponseDto findById(Long id);

    List<AssinaturaResponseDto> findAll();

    AssinaturaResponseDto create(AssinaturaCreateDto assinaturaCreateDto);

    AssinaturaCreateDto update(AssinaturaCreateDto assinaturaCreateDto);

    void delete(Long id);
}
