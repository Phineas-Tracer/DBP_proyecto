package org.ide.dbp_proyecto.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ide.dbp_proyecto.ruta.Dificultad;
import org.ide.dbp_proyecto.ruta.TipoPaisaje;
import org.ide.dbp_proyecto.dto.*;
import org.ide.dbp_proyecto.service.RutaService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rutas")
@RequiredArgsConstructor
public class RutaController {

    private final RutaService rutaService;

    @GetMapping
    public ResponseEntity<PagedResponseDto<RutaResponseDto>> getCatalogo(
            @RequestParam(required = false) Dificultad dificultad,
            @RequestParam(required = false) TipoPaisaje tipoPaisaje,
            @PageableDefault(size = 10, page = 0) Pageable pageable) {
        return ResponseEntity.ok(
                rutaService.obtenerCatalogoPaginado(dificultad, tipoPaisaje, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RutaDetalleResponseDto> getRutaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(rutaService.obtenerRutaPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RutaDetalleResponseDto> crearRuta(@Valid @RequestBody RutaCreateDto rutaCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rutaService.crearRuta(rutaCreateDto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")  // recomendado: proteger también el PUT
    public ResponseEntity<RutaDetalleResponseDto> actualizarRuta(@PathVariable Long id,
            @Valid @RequestBody RutaUpdateDto rutaUpdateDto) {
        return ResponseEntity.ok(rutaService.actualizarRuta(id, rutaUpdateDto));
    }
}