package org.ide.dbp_proyecto.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlanificacionResponseDTO {

    private Long id;

    private String titulo;

    private String notas;

    private LocalDateTime fecha;

    private Boolean disponibilidad;

    private LocalDateTime createdAt;

    private PlanRutaResponseDTO ruta;
}