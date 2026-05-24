package org.ide.dbp_proyecto.Controller;

import org.ide.dbp_proyecto.DTO.ImportacionPoiResponseDto;
import org.ide.dbp_proyecto.Service.PoiImportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/pois")
public class AdminPoiController {

    private final PoiImportService poiImportService;

    public AdminPoiController(PoiImportService poiImportService) {
        this.poiImportService = poiImportService;
    }

    @PostMapping("/importar/{idRuta}")
    public ResponseEntity<ImportacionPoiResponseDto> importarPoisParaRuta(
            @PathVariable Long idRuta,
            @RequestParam(required = false) Double radio,
            @RequestParam(required = false) List<String> tipos
    ) {
        ImportacionPoiResponseDto resultado = poiImportService.importarPoisParaRuta(
                idRuta, radio, tipos
        );
        return ResponseEntity.ok(resultado);
    }
}