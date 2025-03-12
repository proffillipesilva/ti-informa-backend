package br.com.tiinforma.backend.domain.usuarioVideoProgresso;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.Duration;

public record UsuarioVideoProgressoDto(
        @NotNull(message = "O ID do usuário é obrigatório")
        Long usuarioId,

        @NotNull(message = "O ID do vídeo é obrigatório")
        Long videoId,

        @NotNull(message = "O tempo assistido é obrigatório")
        @Positive(message = "O tempo assistido deve ser maior que zero")
        Duration tempoAssistido
) {}