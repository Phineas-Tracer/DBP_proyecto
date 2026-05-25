package org.ide.dbp_proyecto.DTO;

import java.util.List;

public record ImportacionPoiResponseDto(
        Long rutaId,
        int totalEncontrados,
        int importados,
        int duplicadosIgnorados,
        List<String> nombresImportados
) {
}
