package org.ide.dbp_proyecto.Category;

import org.ide.dbp_proyecto.Entities.PuntoInteres;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PuntoInteresRepository extends JpaRepository<PuntoInteres, Long> {
    List<PuntoInteres> findByCategoriaId(Long categoriaId);
}
