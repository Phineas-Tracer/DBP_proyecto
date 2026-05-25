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
public class PlanRutaResponseDTO {

    private Long id;

    private String nombre;

    private List<PoiResponseDTO> pois;
}
