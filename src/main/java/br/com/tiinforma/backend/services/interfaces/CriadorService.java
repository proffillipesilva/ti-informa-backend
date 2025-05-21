package br.com.tiinforma.backend.services.interfaces;

import br.com.tiinforma.backend.domain.criador.CriadorCreateDto;
import br.com.tiinforma.backend.domain.criador.CriadorResponseDto;
import java.util.List;

public interface CriadorService {
    CriadorResponseDto findById(Long id);

    List<CriadorResponseDto> findAll();

    CriadorResponseDto update(Long id,CriadorCreateDto criadorCreateDto);

    void delete(Long id);

    CriadorResponseDto promoverParaCriador(Long id,String cpf, String formacao);
}
