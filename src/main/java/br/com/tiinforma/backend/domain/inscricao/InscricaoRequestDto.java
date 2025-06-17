package br.com.tiinforma.backend.domain.inscricao;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InscricaoRequestDto {
    private Long userId;
    private Long criadorId;
    private boolean inscrever;
}