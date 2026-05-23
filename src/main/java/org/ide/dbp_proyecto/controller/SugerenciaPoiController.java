package org.ide.dbp_proyecto.controller;

import org.ide.dbp_proyecto.dto.SugerenciaPoiDTO;
import org.ide.dbp_proyecto.service.PoiImportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rutas")
public class SugerenciaPoiController {

    private final PoiImportService poiImportService;

    public SugerenciaPoiController(PoiImportService poiImportService) {
        this.poiImportService = poiImportService;
    }

    /**
     * Devuelve POIs cercanos a una Ruta consultando Google Places en vivo.
     *
     * Ejemplos:
     *   GET /api/rutas/1/sugerencias-pois
     *   GET /api/rutas/1/sugerencias-pois?radio=3000
     *   GET /api/rutas/1/sugerencias-pois?radio=2000&tipos=restaurant,tourist_attraction
     */
    @GetMapping("/{idRuta}/sugerencias-pois")
    public ResponseEntity<List<SugerenciaPoiDTO>> obtenerSugerencias(
            @PathVariable Long idRuta,
            @RequestParam(required = false) Double radio,
            @RequestParam(required = false) List<String> tipos
    ) {
        List<SugerenciaPoiDTO> sugerencias = poiImportService.buscarSugerenciasParaRuta(
                idRuta, radio, tipos
        );
        return ResponseEntity.ok(sugerencias);
    }
}