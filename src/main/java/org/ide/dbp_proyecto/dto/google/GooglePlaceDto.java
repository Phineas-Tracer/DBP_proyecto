package org.ide.dbp_proyecto.dto.google;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GooglePlaceDto(
        String id,
        DisplayNameDto displayName,
        String formattedAddress, //Direccion completa
        LocationDto location, //Coordenadas geográficas
        List<String> types, //Categorias
        String primaryType //Categoria principal
) {
}
