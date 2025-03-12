package br.com.tiinforma.backend.domain.video;

import br.com.tiinforma.backend.domain.criador.CriadorInfoBasicaDto;

import java.time.LocalDate;
import java.util.List;

public record VideoResponseDto(
        Long id,
        CriadorInfoBasicaDto infoBasicasCriador,
        String titulo,
        String descricao,
        String url,
        LocalDate dataPublicacao,
        String categoria,
        List<String> palavraChave
) {
}
