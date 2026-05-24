package org.ide.dbp_proyecto.DTO;

import jakarta.validation.constraints.NotNull;

public record CheckinRequestDTO(
    @NotNull Long poiId,
    @NotNull Double latitudUsuario,
    @NotNull Double longitudUsuario
) {
}
