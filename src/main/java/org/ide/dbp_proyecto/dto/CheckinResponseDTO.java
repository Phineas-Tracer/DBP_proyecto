package org.ide.dbp_proyecto.DTO;

import java.time.LocalDateTime;
import java.util.List;

public record CheckinResponseDTO(
    String mensajeEstado,
    String nombreLugar,
    LocalDateTime fechaVisita,
    Double distanciaCalculada,
    List<String> recompensasObtenidas
) {
}
