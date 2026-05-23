package org.ide.dbp_proyecto.dto.google;

import java.util.List;

public record NearbySearchRequestDto(
        List<String> includedTypes,
        Integer maxResultCount,
        LocationRestrictionDto locationRestriction
) {
}