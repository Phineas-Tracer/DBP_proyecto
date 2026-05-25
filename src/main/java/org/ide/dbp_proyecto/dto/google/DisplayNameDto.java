package org.ide.dbp_proyecto.dto.google;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DisplayNameDto(
        String text,
        String languageCode
) {
}
