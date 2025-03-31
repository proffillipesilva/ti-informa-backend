package br.com.tiinforma.backend.services.interfaces;

import br.com.tiinforma.backend.domain.assinatura.AssinaturaCreateDto;
import br.com.tiinforma.backend.domain.assinatura.AssinaturaDto;

import java.util.List;

public interface AssinaturaService {
    AssinaturaDto findById(Long id);

    List<AssinaturaDto> findAll();

    AssinaturaDto create(AssinaturaCreateDto assinaturaCreateDto);

    AssinaturaCreateDto update(AssinaturaCreateDto assinaturaCreateDto);

    void delete(Long id);
}
