package br.com.tiinforma.backend.domain.criador;

public record CriadorResponseDto(
        Long id,
        String nome,
        String email,
        String formacao
) {
}
