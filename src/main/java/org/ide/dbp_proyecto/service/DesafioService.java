package org.ide.dbp_proyecto.Service;

import lombok.RequiredArgsConstructor;
import org.ide.dbp_proyecto.DTO.DesafioRequestDTO;
import org.ide.dbp_proyecto.DTO.DesafioResponseDTO;
import org.ide.dbp_proyecto.DTO.PlanificacionRequestDTO;
import org.ide.dbp_proyecto.DTO.PlanificacionResponseDTO;
import org.ide.dbp_proyecto.Repository.DesafioRepository;
import org.ide.dbp_proyecto.entity.Desafio;
import org.ide.dbp_proyecto.entity.Planificacion;
import org.ide.dbp_proyecto.entity.User;
import org.ide.dbp_proyecto.exception.BadCredentialsException;
import org.ide.dbp_proyecto.exception.ConflictException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DesafioService {
    private final DesafioRepository repository;
    private final ModelMapper modelMapper;

    public DesafioResponseDTO createReto(DesafioRequestDTO request, User usuario) {
        if (usuario == null)
            throw new BadCredentialsException("Usuario no autenticado");
        if (repository.existsByTitleAndUsuario(request.getTitle(), usuario))
            throw new ConflictException("Ya existe un Desafio con características similares");
        Desafio reto = convertirDtoAEntidad(request);
        reto.setUsuario(usuario);
        reto.setValue(request.getType().getValor());
        Desafio response = repository.save(reto);
        return convertirEntidadADto(response);
    }

    public DesafioResponseDTO convertirEntidadADto(Desafio Desafio) {
        return modelMapper.map(Desafio, DesafioResponseDTO.class);
    }

    public Desafio convertirDtoAEntidad(DesafioRequestDTO dto) {
        return modelMapper.map(dto, Desafio.class);
    }

}
