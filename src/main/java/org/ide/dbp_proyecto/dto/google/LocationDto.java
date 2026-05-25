package org.ide.dbp_proyecto.dto.google;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LocationDto(
        Double latitude,
        Double longitude
) {
}
