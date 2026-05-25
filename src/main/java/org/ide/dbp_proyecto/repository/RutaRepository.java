package org.ide.dbp_proyecto.repository;

import org.ide.dbp_proyecto.ruta.Dificultad;
import org.ide.dbp_proyecto.ruta.TipoPaisaje;
import org.ide.dbp_proyecto.entity.Ruta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RutaRepository extends JpaRepository<Ruta, Long> {
    @Query("SELECT r FROM Ruta r WHERE " +
            "(:dificultad IS NULL OR r.dificultad = :dificultad) AND " +
            "(:tipoPaisaje IS NULL OR r.tipoPaisaje = :tipoPaisaje)")
    Page<Ruta> buscarConFiltrosOpcionales(
            @Param("dificultad") Dificultad dificultad,
            @Param("tipoPaisaje") TipoPaisaje tipoPaisaje,
            Pageable pageable
    );
}
