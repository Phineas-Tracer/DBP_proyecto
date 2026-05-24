package org.ide.dbp_proyecto.DTO;

import lombok.Getter;

import java.util.List;

@Getter
public class AddPoisRequestDTO {
    private Long rutaId;
    private List<SugerenciaPoiDTO> pois;
}
