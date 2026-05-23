package org.ide.dbp_proyecto.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CoordenadaDto {

    @NotNull(message = "La latitud es obligatoria")
    private Double latitud;

    @NotNull(message = "La longitud es obligatoria")
    private Double longitud;
}