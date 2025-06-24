package br.com.tiinforma.backend.repositories;

import br.com.tiinforma.backend.domain.inscricao.Inscritos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface InscritosRepository extends JpaRepository<Inscritos, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM Inscritos i WHERE i.usuario.id = :usuarioId AND i.criador.id = :criadorId")
    void deleteByUsuarioIdAndCriadorId(@Param("usuarioId") Long usuarioId,
                                       @Param("criadorId") Long criadorId);

    int countByCriadorId(Long criadorId);

    boolean existsByUsuarioIdAndCriadorId(Long usuarioId, Long criadorId);
}