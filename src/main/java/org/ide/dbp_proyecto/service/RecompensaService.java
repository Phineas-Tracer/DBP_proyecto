package org.ide.dbp_proyecto.Service;

import lombok.RequiredArgsConstructor;
import org.ide.dbp_proyecto.DTO.PlanificacionRequestDTO;
import org.ide.dbp_proyecto.DTO.PlanificacionResponseDTO;
import org.ide.dbp_proyecto.DTO.RewardRequestDTO;
import org.ide.dbp_proyecto.DTO.RewardResponseDTO;
import org.ide.dbp_proyecto.Repository.DesafioRepository;
import org.ide.dbp_proyecto.Repository.RecompensaRepository;
import org.ide.dbp_proyecto.Repository.UserRepository;
import org.ide.dbp_proyecto.User.Role;
import org.ide.dbp_proyecto.entity.Desafio;
import org.ide.dbp_proyecto.entity.Planificacion;
import org.ide.dbp_proyecto.entity.Recompensa;
import org.ide.dbp_proyecto.entity.User;
import org.ide.dbp_proyecto.exception.AccessDeniedException;
import org.ide.dbp_proyecto.exception.BadCredentialsException;
import org.ide.dbp_proyecto.exception.ConflictException;
import org.ide.dbp_proyecto.exception.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecompensaService {
    private final RecompensaRepository repository;
    private final ModelMapper modelMapper;
    private final DesafioRepository desafioRepository;
    private final UserRepository userRepository;

    public RewardResponseDTO createRecompensa(RewardRequestDTO request, User usuario) {
        if (usuario == null)
            throw new BadCredentialsException("Usuario no autenticado");
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

    public Page<RewardResponseDTO> getMyRewards(User usuario, Pageable pageable) {
        if (usuario == null)
            throw new BadCredentialsException("Usuario no autenticado");
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
