package org.ide.dbp_proyecto.controller;

import lombok.RequiredArgsConstructor;
import org.ide.dbp_proyecto.entity.PuntoInteres;
import org.ide.dbp_proyecto.service.PoiService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PoiController {
    private final PoiService poiService;

    @PostMapping(value = "/pois", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PuntoInteres> createPoi(
            @RequestParam("nombre") String nombre,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("latitud") Double latitud,
            @RequestParam("longitud") Double longitud,
            @RequestParam("categoriaId") Long categoriaId,
            @RequestPart("imagen") MultipartFile imagen
    ) throws IOException {
        PuntoInteres nuevoPoi = poiService.createPoi(nombre, descripcion, latitud, longitud, categoriaId, imagen);
        return ResponseEntity.ok(nuevoPoi);
    }

    @GetMapping("/pois")
    public ResponseEntity<List<PuntoInteres>> getPois(@RequestParam(required = false) Long categoriaId) {
        return ResponseEntity.ok(poiService.getPois(categoriaId));
    }

    @PostMapping("/rutas/{idRuta}/pois/{idPoi}")
    public ResponseEntity<String> asociarPoiARuta(@PathVariable Long idRuta, @PathVariable Long idPoi) {
        poiService.asociarPoiARuta(idRuta, idPoi);
        return ResponseEntity.ok("POI asociado a la ruta correctamente");
    }

    @GetMapping("/rutas/{idRuta}/pois")
    public ResponseEntity<List<PuntoInteres>> getPoisByRuta(@PathVariable Long idRuta) {
        return ResponseEntity.ok(poiService.getPoisByRuta(idRuta));
    }
}
