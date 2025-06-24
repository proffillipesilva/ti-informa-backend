package br.com.tiinforma.backend.services.implementations;

import br.com.tiinforma.backend.repositories.InscritosRepository;
import br.com.tiinforma.backend.services.interfaces.InscritosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InscritosImpl implements InscritosService {

    @Autowired
    private InscritosRepository inscritosRepository;

    @Override
    public boolean verificarInscricao(Long userId, Long creatorId) {
        return inscritosRepository.existsByUsuarioIdAndCriadorId(userId, creatorId);
    }
}