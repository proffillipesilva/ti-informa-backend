package br.com.tiinforma.backend.repositories;

import br.com.tiinforma.backend.domain.avaliacao.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {
}
