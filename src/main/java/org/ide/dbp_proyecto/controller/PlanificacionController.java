package org.ide.dbp_proyecto.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ide.dbp_proyecto.dto.*;
import org.ide.dbp_proyecto.service.PlanificacionService;
import org.ide.dbp_proyecto.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/planificaciones")
@RequiredArgsConstructor
public class PlanificacionController {
    private final PlanificacionService service;

    @PostMapping
    public ResponseEntity<PlanificacionResponseDTO> createPlan(@Valid @RequestBody PlanificacionRequestDTO request, Authentication authentication) {
        PlanificacionResponseDTO response = service.createPlanning(request, authentication.getName());
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/mis-planes")
    public ResponseEntity<PagedResponseDto<PlanificacionResponseDTO>> getMyPlans(Authentication authentication, @PageableDefault(page = 0, size = 10) Pageable pageable) {
        Page<PlanificacionResponseDTO> plansPage = service.getMyPlans(authentication.getName(), pageable);
        PagedResponseDto<PlanificacionResponseDTO> response = new PagedResponseDto<>(plansPage);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/pois")
    public ResponseEntity<PlanificacionResponseDTO> addPoisToPlanning(@PathVariable Long id, @Valid @RequestBody AddPoisRequestDTO request,
                                                                      Authentication authentication) {
        PlanificacionResponseDTO response = service.addPoisToPlanning(id, request, authentication.getName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/export")
    public ResponseEntity<PlanningExportDTO> exportPlanning(@PathVariable Long id, Authentication authentication) {
        PlanningExportDTO response = service.exportPlanning(id, authentication.getName());
        return ResponseEntity.ok(response);
    }



}
