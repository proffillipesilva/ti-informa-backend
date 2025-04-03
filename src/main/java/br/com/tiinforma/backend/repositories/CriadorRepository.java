package br.com.tiinforma.backend.repositories;

import br.com.tiinforma.backend.domain.criador.Criador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CriadorRepository extends JpaRepository<Criador, Long> {
    Optional<Criador> findByEmail(String email);
}
