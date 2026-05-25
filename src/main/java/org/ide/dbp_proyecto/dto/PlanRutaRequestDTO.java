package org.ide.dbp_proyecto.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlanRutaRequestDTO {
    private String nombre;
    private List<SugerenciaPoiDTO> pois;
}