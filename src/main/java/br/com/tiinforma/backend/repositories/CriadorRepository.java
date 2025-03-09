package br.com.tiinforma.backend.repositories;

import br.com.tiinforma.backend.domain.entities.Assinatura;
import br.com.tiinforma.backend.domain.entities.Criador;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CriadorRepository extends JpaRepository<Criador, Long> {
}
