package org.ide.dbp_proyecto.Category;

import org.ide.dbp_proyecto.Entities.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
