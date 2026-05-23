package org.ide.dbp_proyecto.dto.google;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GooglePlacesResponseDto(
        List<GooglePlaceDto> places
) { }
