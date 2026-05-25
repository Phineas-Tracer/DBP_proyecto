package org.ide.dbp_proyecto.service;

import lombok.RequiredArgsConstructor;
import org.ide.dbp_proyecto.dto.*;
import org.ide.dbp_proyecto.repository.PlanificacionRepository;
import org.ide.dbp_proyecto.repository.RutaRepository;
import org.ide.dbp_proyecto.repository.UserRepository;
import org.ide.dbp_proyecto.entity.Planificacion;
import org.ide.dbp_proyecto.entity.PuntoInteres;
import org.ide.dbp_proyecto.entity.Ruta;
import org.ide.dbp_proyecto.entity.User;
import org.ide.dbp_proyecto.exception.AccessDeniedException;
import org.ide.dbp_proyecto.exception.BadCredentialsException;
import org.ide.dbp_proyecto.exception.ConflictException;
import org.ide.dbp_proyecto.exception.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanificacionService {
    private final PlanificacionRepository repository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final RutaRepository rutaRepository;
    private final PoiImportService poiImportService;

    public PlanificacionResponseDTO createPlanning(PlanificacionRequestDTO request, String email) {
        User usuario = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        if (repository.existsByTitleAndUsuario(request.getTitulo(), usuario))
            throw new ConflictException("Ya existe una planificación con aquellas características");
        Planificacion plan = convertirDtoAEntidad(request);
        Ruta ruta = new Ruta();
        ruta.setNombre("Ruta de " + request.getTitulo());
        List<PuntoInteres> poisPersistidos = new ArrayList<>();

        for (SugerenciaPoiDTO poiDTO : request.getRuta().getPois()) {
            PuntoInteres poi = poiImportService.persistirPoiDesdeGoogle(poiDTO);
            poisPersistidos.add(poi);
        }
        ruta.setPuntosDeInteres(poisPersistidos);
        Ruta finalRoute = rutaRepository.save(ruta);
        plan.getRutas().add(finalRoute);
        plan.setUsuario(usuario);

        plan.setCreatedAt(LocalDateTime.now());
        plan.setDisponibilidad(true);
        Planificacion finalPlan = repository.save(plan);
        return convertirEntidadADto(finalPlan);
    }

    public Page<PlanificacionResponseDTO> getMyPlans(String email, Pageable pageable) {
        User usuario = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        Page<Planificacion> plansPage = repository.findByUsuario(usuario, pageable);
        return plansPage.map(this::convertirEntidadADto);
    }

    public PlanificacionResponseDTO addPoisToPlanning(Long planId, AddPoisRequestDTO request, String email) {
        User usuario = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        Planificacion plan = repository.findById(planId).orElseThrow(() -> new ResourceNotFoundException("Planificacion no encontrada"));
        if (!plan.getUsuario().getId().equals(usuario.getId()))
            throw new AccessDeniedException("No puedes modificar esta planificacion");
        Ruta ruta = plan.getRutas().stream().filter(r -> r.getId().equals(request.getRutaId())).findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Ruta no encontrada"));

        for (SugerenciaPoiDTO poiDTO : request.getPois()) {
            PuntoInteres poi = poiImportService.persistirPoiDesdeGoogle(poiDTO);
            ruta.getPuntosDeInteres().add(poi);
        }
        rutaRepository.save(ruta);
        return convertirEntidadADto(plan);
    }

    public PlanningExportDTO exportPlanning(Long planId, String email) {
        User usuario = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        Planificacion plan = repository.findById(planId).orElseThrow(() -> new ResourceNotFoundException("Planificacion no encontrada"));
        if (!plan.getUsuario().getId().equals(usuario.getId()))
            throw new AccessDeniedException("No puedes exportar esta planificaccion");
        return convertirEntidadAExportDto(plan);
    }

    public PlanningExportDTO convertirEntidadAExportDto(Planificacion planificacion) {
        return modelMapper.map(planificacion, PlanningExportDTO.class);
    }

    public PlanificacionResponseDTO convertirEntidadADto(Planificacion planificacion) {
        return modelMapper.map(planificacion, PlanificacionResponseDTO.class);
    }

    public Planificacion convertirDtoAEntidad(PlanificacionRequestDTO dto) {
        return modelMapper.map(dto, Planificacion.class);
    }

}
