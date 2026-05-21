package org.ide.dbp_proyecto.repository;

import org.ide.dbp_proyecto.Temporales.PuntoDeInteres;
import org.ide.dbp_proyecto.Temporales.Usuario;
import org.ide.dbp_proyecto.entity.LugarColeccionado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LugarColeccionadoRepository extends JpaRepository<LugarColeccionado, Long> {
    boolean existsByUsuarioAndPuntoDeInteres(Usuario usuario, PuntoDeInteres puntoDeInteres);
}
