package org.ide.dbp_proyecto.repository;

import org.ide.dbp_proyecto.entity.Planificacion;
import org.ide.dbp_proyecto.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanificacionRepository extends JpaRepository<Planificacion, Long> {
    Boolean existsByTitleAndUsuario(String title, User usuario);
    Page<Planificacion> findByUsuario(User usuario, Pageable pageable);

}
