package org.ide.dbp_proyecto.dto;

import lombok.Getter;
import org.ide.dbp_proyecto.enums.TipoDesafio;

@Getter
public class DesafioRequestDTO {
    private String title;
    private String description;
    private TipoDesafio type;
}
