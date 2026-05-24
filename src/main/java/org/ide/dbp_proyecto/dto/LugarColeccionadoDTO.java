package org.ide.dbp_proyecto.DTO;

import java.time.LocalDateTime;

public record LugarColeccionadoDTO(
    Long poiId,
    String nombrePoi,
    String categoriaPoi,       // "Cascada", "Gastronomía", etc.
    String imagenUrlPoi,       // URL de Cloudinary
    LocalDateTime fechaVisita,
    Double latitudCheckin,
    Double longitudCheckin
) {
}
