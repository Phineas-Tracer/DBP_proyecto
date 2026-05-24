package org.ide.dbp_proyecto.DTO.google;

import java.util.List;

public record NearbySearchRequestDto(
        List<String> includedTypes,
        Integer maxResultCount,
        LocationRestrictionDto locationRestriction
) {
}