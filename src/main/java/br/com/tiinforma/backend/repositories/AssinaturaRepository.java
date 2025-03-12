package br.com.tiinforma.backend.repositories;

import br.com.tiinforma.backend.domain.assinatura.Assinatura;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssinaturaRepository extends JpaRepository<Assinatura, Long> {
}
