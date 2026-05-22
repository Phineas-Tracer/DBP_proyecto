package org.ide.dbp_proyecto.repository;

import org.ide.dbp_proyecto.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
