package org.ide.dbp_proyecto.Repository;

import org.ide.dbp_proyecto.entity.Desafio;
import org.ide.dbp_proyecto.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DesafioRepository extends JpaRepository<Desafio, Long> {
    boolean existsByTitleAndUsuario(String titulo, User usuario);
}
