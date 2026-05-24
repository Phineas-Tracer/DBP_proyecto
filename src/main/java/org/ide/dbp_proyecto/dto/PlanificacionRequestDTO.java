package org.ide.dbp_proyecto.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlanificacionRequestDTO {
    private String titulo;
    private String notas;
    private LocalDateTime fecha;
    private PlanRutaRequestDTO ruta;
}
