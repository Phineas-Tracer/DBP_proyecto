package org.ide.dbp_proyecto.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ide.dbp_proyecto.DTO.PagedResponseDto;
import org.ide.dbp_proyecto.DTO.RewardRequestDTO;
import org.ide.dbp_proyecto.DTO.RewardResponseDTO;
import org.ide.dbp_proyecto.Service.RecompensaService;
import org.ide.dbp_proyecto.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recompensas")
@RequiredArgsConstructor
public class RecompensaController {
    private final RecompensaService service;

    @PostMapping
    public ResponseEntity<RewardResponseDTO> createReward(@Valid @RequestBody RewardRequestDTO request, @AuthenticationPrincipal User usuario) {
        RewardResponseDTO response = service.createRecompensa(request, usuario);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/mis-recompensas")
    public ResponseEntity<PagedResponseDto<RewardResponseDTO>> getMyRewards(@AuthenticationPrincipal User usuario,
                                                                            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        Page<RewardResponseDTO> rewardsPage = service.getMyRewards(usuario, pageable);
        PagedResponseDto<RewardResponseDTO> response = new PagedResponseDto<>(rewardsPage);
        return ResponseEntity.ok(response);
    }
}
