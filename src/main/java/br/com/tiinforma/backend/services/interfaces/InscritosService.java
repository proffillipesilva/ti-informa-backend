package br.com.tiinforma.backend.services.interfaces;

public interface InscritosService {
    boolean verificarInscricao(Long userId, Long creatorId);
}