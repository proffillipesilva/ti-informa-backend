package br.com.tiinforma.backend.domain.assinatura;

import br.com.tiinforma.backend.domain.enums.Plano;

import java.time.LocalDate;

public record AssinaturaDto(
        Long id,
        Long idUsuario,
        Plano plano,
        LocalDate dataInicio,
        LocalDate dataFim,
        Double preco
) {
}
