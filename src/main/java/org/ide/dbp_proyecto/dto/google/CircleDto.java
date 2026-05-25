package org.ide.dbp_proyecto.dto.google;

import org.ide.dbp_proyecto.dto.google.LocationDto;

public record CircleDto(
        LocationDto center,
        Double radius
) {
}
