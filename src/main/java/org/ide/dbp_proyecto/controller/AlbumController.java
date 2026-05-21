package org.ide.dbp_proyecto.controller;

import org.ide.dbp_proyecto.Temporales.Usuario;
import org.ide.dbp_proyecto.dto.CheckinRequestDTO;
import org.ide.dbp_proyecto.dto.CheckinResponseDTO;
import org.ide.dbp_proyecto.repository.LugarColeccionadoRepository;
import org.ide.dbp_proyecto.service.AlbumService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.function.EntityResponse;

@RestController
@RequestMapping("/album")
public class AlbumController {

    private final AlbumService albumService;

    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @PostMapping("/checkin")
    public ResponseEntity<CheckinResponseDTO> checkin(@RequestBody CheckinRequestDTO dto, Usuario user) {
        CheckinResponseDTO response = albumService.realizarCheckin(dto, user);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
