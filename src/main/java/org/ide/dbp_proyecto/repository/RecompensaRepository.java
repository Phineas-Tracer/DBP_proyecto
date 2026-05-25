package org.ide.dbp_proyecto.repository;

import org.ide.dbp_proyecto.entity.Recompensa;
import org.ide.dbp_proyecto.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecompensaRepository extends JpaRepository<Recompensa, Long> {
    Boolean existsByName(String nombre);
    Page<Recompensa> findByUsuariosContains(User usuario, Pageable pageable);
}
