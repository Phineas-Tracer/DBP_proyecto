package org.ide.dbp_proyecto.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.ide.dbp_proyecto.ruta.Dificultad;
import org.ide.dbp_proyecto.ruta.TipoPaisaje;

import java.util.List;

@Getter
@Setter
public class RutaUpdateDto {

    @NotBlank(message = "El nombre modificado no puede estar vacío")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String nombre;

    @NotNull(message = "La latitud del centro es obligatoria")
    private Double latitudCentro;

    @NotNull(message = "La longitud del centro es obligatoria")
    private Double longitudCentro;

    @NotNull(message = "La dificultad es obligatoria")
    private Dificultad dificultad;

    @NotNull(message = "El tipo de paisaje es obligatorio")
    private TipoPaisaje tipoPaisaje;

    @NotNull(message = "El trazado geográfico no puede quedar vacío")
    @Size(min = 2, message = "El trazado debe tener al menos inicio y fin")
    private List<CoordenadaDto> trazadoGeografico;

    private List<Long> puntosDeInteresIds;
}
