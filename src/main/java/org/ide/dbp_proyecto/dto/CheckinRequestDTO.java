package org.ide.dbp_proyecto.dto;

import jakarta.validation.constraints.NotNull;

public record CheckinRequestDTO(
    Long poiId,
    @NotNull Double latitudUsuario,
    @NotNull Double longitudUsuario
) {
}
