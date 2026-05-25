package org.ide.dbp_proyecto.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ide.dbp_proyecto.DTO.DesafioRequestDTO;
import org.ide.dbp_proyecto.DTO.DesafioResponseDTO;
import org.ide.dbp_proyecto.Service.DesafioService;
import org.ide.dbp_proyecto.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/retos")
@RequiredArgsConstructor
public class DesafioController {
    private final DesafioService service;

    @PostMapping
    public ResponseEntity<DesafioResponseDTO> crearReto(@Valid @RequestBody DesafioRequestDTO resqest, @AuthenticationPrincipal User usuario) {
        DesafioResponseDTO response = service.createReto(resqest, usuario);
        return ResponseEntity.status(201).body(response);
    }



}

