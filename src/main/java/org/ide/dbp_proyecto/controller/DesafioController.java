package org.ide.dbp_proyecto.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ide.dbp_proyecto.dto.DesafioRequestDTO;
import org.ide.dbp_proyecto.dto.DesafioResponseDTO;
import org.ide.dbp_proyecto.service.DesafioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    public ResponseEntity<DesafioResponseDTO> crearReto(@Valid @RequestBody DesafioRequestDTO request, Authentication authentication) {
        DesafioResponseDTO response = service.createReto(request, authentication.getName());
        return ResponseEntity.status(201).body(response);
    }

}

