package org.ide.dbp_proyecto.dto;

import lombok.Data;

import java.util.List;

@Data
public class RutaDetalleResponseDto {
    private Long id;
    private String nombre;
    private Double latitudCentro;
    private Double longitudCentro;

    private String dificultad;
    private String tipoPaisaje;

    private List<CoordenadaDto> trazadoGeografico;

    private List<PuntoInteresResponseDto> puntosDeInteres;
}