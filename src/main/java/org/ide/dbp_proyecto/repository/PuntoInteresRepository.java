package org.ide.dbp_proyecto.Repository;

import org.ide.dbp_proyecto.entity.PuntoInteres;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PuntoInteresRepository extends JpaRepository<PuntoInteres, Long> {
    List<PuntoInteres> findByCategoriaId(Long categoriaId);
    Optional<PuntoInteres> findById(Long id);

    //Google Places
    Boolean existsByGooglePlaceId(String googlePlaceId);
    Optional<PuntoInteres> findByGooglePlaceId(String googlePlaceId);
}
