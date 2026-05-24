package org.ide.dbp_proyecto.DTO;

public record SugerenciaPoiDTO(
        String googlePlaceId,
        String nombre,
        String direccion,
        Double latitud,
        Double longitud,
        String categoria,
        String tipoGoogle
) {
}
