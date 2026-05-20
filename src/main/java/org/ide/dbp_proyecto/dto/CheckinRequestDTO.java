package org.ide.dbp_proyecto.dto;

import jakarta.validation.constraints.NotNull;

public record CheckinRequestDTO(
    Long id,
    @NotNull Double latitudUsuario,
    @NotNull Double longitudUsuario
) {
}
