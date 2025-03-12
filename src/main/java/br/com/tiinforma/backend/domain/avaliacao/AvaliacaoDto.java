package br.com.tiinforma.backend.domain.avaliacao;

public record AvaliacaoDto(
        Long id,
        Integer nota,
        String comentario
) {
}
