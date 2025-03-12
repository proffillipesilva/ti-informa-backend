package br.com.tiinforma.backend.repositories;

import br.com.tiinforma.backend.domain.usuarioVideoProgresso.UsuarioVideoProgresso;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioVideoProgressoRepository extends JpaRepository<UsuarioVideoProgresso, Long> {
}

