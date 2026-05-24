package org.ide.dbp_proyecto.DTO;

import lombok.Getter;
import org.ide.dbp_proyecto.Enums.TipoDesafio;

@Getter
public class DesafioRequestDTO {
    private String title;
    private String description;
    private TipoDesafio type;
}
