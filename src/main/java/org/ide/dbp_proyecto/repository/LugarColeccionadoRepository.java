package org.ide.dbp_proyecto.repository;

import org.ide.dbp_proyecto.entity.PuntoInteres;
import org.ide.dbp_proyecto.entity.LugarColeccionado;
import org.ide.dbp_proyecto.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LugarColeccionadoRepository extends JpaRepository<LugarColeccionado, Long> {
    boolean existsByUsuarioAndPuntoDeInteres(User usuario, PuntoInteres puntoDeInteres);
    List<LugarColeccionado> findByUsuario(User usuario);
}

