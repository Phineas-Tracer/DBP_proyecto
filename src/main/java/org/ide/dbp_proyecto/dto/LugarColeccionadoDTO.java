package org.ide.dbp_proyecto.dto;

import java.time.LocalDateTime;

public record LugarColeccionadoDTO(
    Long id,
    String nombrePoi,
    String categoriaPoi,       // "Cascada", "Gastronomía", etc.
    String imagenUrlPoi,       // URL de Supabase Storage (del Integrante 3)
    LocalDateTime fechaVisita,
    Double latitudCheckin,
    Double longitudCheckin
) {
}
