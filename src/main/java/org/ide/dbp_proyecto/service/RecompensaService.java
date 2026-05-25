package org.ide.dbp_proyecto.service;

import lombok.RequiredArgsConstructor;
import org.ide.dbp_proyecto.dto.RewardRequestDTO;
import org.ide.dbp_proyecto.dto.RewardResponseDTO;
import org.ide.dbp_proyecto.repository.DesafioRepository;
import org.ide.dbp_proyecto.repository.RecompensaRepository;
import org.ide.dbp_proyecto.repository.UserRepository;
import org.ide.dbp_proyecto.user.Role;
import org.ide.dbp_proyecto.entity.Desafio;
import org.ide.dbp_proyecto.entity.Recompensa;
import org.ide.dbp_proyecto.entity.User;
import org.ide.dbp_proyecto.exception.AccessDeniedException;
import org.ide.dbp_proyecto.exception.ConflictException;
import org.ide.dbp_proyecto.exception.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecompensaService {
    private final RecompensaRepository repository;
    private final ModelMapper modelMapper;
    private final DesafioRepository desafioRepository;
    private final UserRepository userRepository;

    public RewardResponseDTO createRecompensa(RewardRequestDTO request, String email) {
        User usuario = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        if (usuario.getRole()!= Role.ADMIN)
            throw new AccessDeniedException("No tienes permisos para crear recompensas");
        if (repository.existsByName(request.getName()))
            throw new ConflictException("Esta recompensa ya existe");
        Desafio desafio = desafioRepository.findById(request.getDesafioId()).orElseThrow(() -> new ResourceNotFoundException("Desafio no Encontrado"));
        Recompensa reward = convertirDtoAEntidad(request);
        reward.setDesafio(desafio);
        reward.setImagenBadgeUrl(request.getImagenBadgeUrl());
        reward.setPuntosXp(desafio.getValue());
        Recompensa response = repository.save(reward);
        return convertirEntidadADto(response);
    }

    public Page<RewardResponseDTO> getMyRewards(String email, Pageable pageable) {
        User usuario = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        User user = userRepository.findById(usuario.getId()).orElseThrow(()-> new ResourceNotFoundException("Usuario no encontrado"));
        Page<Recompensa> rewardsPage = repository.findByUsuariosContains(user, pageable);
        return rewardsPage.map(this::convertirEntidadADto);
    }

    public RewardResponseDTO convertirEntidadADto(Recompensa recompensa) {
        return modelMapper.map(recompensa, RewardResponseDTO.class);
    }

    public Recompensa convertirDtoAEntidad(RewardRequestDTO dto) {
        return modelMapper.map(dto, Recompensa.class);
    }


}
