package br.com.tiinforma.backend.repositories;

import br.com.tiinforma.backend.domain.criador.Criador;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CriadorRepository extends JpaRepository<Criador, Long> {
}
