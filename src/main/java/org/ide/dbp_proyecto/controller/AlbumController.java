package org.ide.dbp_proyecto.controller;

import jakarta.validation.Valid;
import org.ide.dbp_proyecto.dto.CheckinRequestDTO;
import org.ide.dbp_proyecto.dto.CheckinResponseDTO;
import org.ide.dbp_proyecto.dto.LugarColeccionadoDTO;
import org.ide.dbp_proyecto.service.AlbumService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/album")
public class AlbumController {

    private final AlbumService albumService;

    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @PostMapping("/checkin")
    public ResponseEntity<CheckinResponseDTO> checkin(@Valid @RequestBody CheckinRequestDTO dto, Authentication auth) {
        CheckinResponseDTO response = albumService.realizarCheckin(dto, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/mi-coleccion")
    public ResponseEntity<List<LugarColeccionadoDTO>> getMiColeccion(Authentication auth) {
        List<LugarColeccionadoDTO> response = albumService.obtenerMiColeccion(auth.getName());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
