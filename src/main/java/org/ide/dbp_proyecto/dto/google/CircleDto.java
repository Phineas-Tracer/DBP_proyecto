package org.ide.dbp_proyecto.DTO.google;

import org.ide.dbp_proyecto.DTO.google.LocationDto;

public record CircleDto(
        LocationDto center,
        Double radius
) {
}
