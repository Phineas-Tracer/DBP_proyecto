package org.ide.dbp_proyecto.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class PlanningExportDTO {
    private Long id;
    private String title;
    private String notas;
    private LocalDate fecha;
    private boolean disponibilidad;
    private LocalDateTime createdAt;
    private List<RutaExportDTO> rutas;
}