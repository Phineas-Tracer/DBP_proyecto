package org.ide.dbp_proyecto.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PoiResponseDTO {

    private Long id;

    private String nombre;

    private String direccion;

    private Double latitud;

    private Double longitud;

    private String googlePlaceId;
}