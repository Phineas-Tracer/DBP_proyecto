package org.ide.dbp_proyecto.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ide.dbp_proyecto.DTO.*;
import org.ide.dbp_proyecto.Service.PlanificacionService;
import org.ide.dbp_proyecto.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/planificaciones")
@RequiredArgsConstructor
public class PlanificacionController {
    private final PlanificacionService service;

    @PostMapping
    public ResponseEntity<PlanificacionResponseDTO> createPlan(@Valid @RequestBody PlanificacionRequestDTO request, @AuthenticationPrincipal User persona) {
        PlanificacionResponseDTO response = service.createPlanning(request, persona);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/mis-planes")
    public ResponseEntity<PagedResponseDto<PlanificacionResponseDTO>> getMyPlans(@AuthenticationPrincipal User usuario, @PageableDefault(page = 0, size = 10) Pageable pageable) {
        Page<PlanificacionResponseDTO> plansPage = service.getMyPlans(usuario, pageable);
        PagedResponseDto<PlanificacionResponseDTO> response = new PagedResponseDto<>(plansPage);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/pois")
    public ResponseEntity<PlanificacionResponseDTO> addPoisToPlanning(@PathVariable Long id, @Valid @RequestBody AddPoisRequestDTO request,
                                                                      @AuthenticationPrincipal User usuario) {
        PlanificacionResponseDTO response = service.addPoisToPlanning(id, request, usuario);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/export")
    public ResponseEntity<PlanningExportDTO> exportPlanning(@PathVariable Long id, @AuthenticationPrincipal User usuario) {
        PlanningExportDTO response = service.exportPlanning(id, usuario);
        return ResponseEntity.ok(response);
    }



}
