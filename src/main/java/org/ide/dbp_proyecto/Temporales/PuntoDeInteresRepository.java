package org.ide.dbp_proyecto.Temporales;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PuntoDeInteresRepository extends JpaRepository<PuntoDeInteres, Long> {
    Optional<PuntoDeInteres> findById(Long id);
}
