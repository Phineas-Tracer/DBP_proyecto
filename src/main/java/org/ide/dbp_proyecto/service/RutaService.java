package org.ide.dbp_proyecto.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.ide.dbp_proyecto.ruta.Dificultad;
import org.ide.dbp_proyecto.ruta.TipoPaisaje;
import org.ide.dbp_proyecto.dto.*;
import org.ide.dbp_proyecto.entity.Coordenada;
import org.ide.dbp_proyecto.entity.PuntoInteres;
import org.ide.dbp_proyecto.entity.Ruta;
import org.ide.dbp_proyecto.exception.ResourceNotFoundException;
import org.ide.dbp_proyecto.repository.PuntoInteresRepository;
import org.ide.dbp_proyecto.repository.RutaRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class RutaService {

    private final RutaRepository rutaRepository;
    private final PuntoInteresRepository puntoInteresRepository;
    private final ModelMapper modelMapper;

    public PagedResponseDto<RutaResponseDto> obtenerCatalogoPaginado(Dificultad dificultad, TipoPaisaje tipoPaisaje, Pageable pageable) {
        Page<Ruta> rutaPage = rutaRepository.buscarConFiltrosOpcionales(dificultad, tipoPaisaje, pageable);
        Page<RutaResponseDto> dtoPage = rutaPage.map(ruta -> modelMapper.map(ruta, RutaResponseDto.class));
        return new PagedResponseDto<>(dtoPage);
    }

    public RutaDetalleResponseDto obtenerRutaPorId(Long id) {
        Ruta ruta = rutaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la ruta con el ID: " + id));
        return mapToDetalleDto(ruta);
    }

    @Transactional
    public RutaDetalleResponseDto crearRuta(RutaCreateDto dto) {
        Ruta nuevaRuta = new Ruta();
        nuevaRuta.setNombre(dto.getNombre());
        nuevaRuta.setLatitudCentro(dto.getLatitudCentro());
        nuevaRuta.setLongitudCentro(dto.getLongitudCentro());
        nuevaRuta.setDificultad(dto.getDificultad());
        nuevaRuta.setTipoPaisaje(dto.getTipoPaisaje());

        // Construir trazado asignando orden según posición en la lista
        List<Coordenada> trazado = buildTrazado(dto.getTrazadoGeografico(), nuevaRuta);
        nuevaRuta.setTrazadoGeografico(trazado);

        // Vincular POIs si se enviaron IDs
        if (dto.getPuntosDeInteresIds() != null && !dto.getPuntosDeInteresIds().isEmpty()) {
            List<PuntoInteres> pois = fetchPois(dto.getPuntosDeInteresIds());
            nuevaRuta.setPuntosDeInteres(pois);
        }

        return mapToDetalleDto(rutaRepository.save(nuevaRuta));
    }

    @Transactional
    public RutaDetalleResponseDto actualizarRuta(Long id, RutaUpdateDto dto) {
        Ruta ruta = rutaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se puede actualizar. No existe la ruta con ID: " + id));

        ruta.setNombre(dto.getNombre());
        ruta.setLatitudCentro(dto.getLatitudCentro());
        ruta.setLongitudCentro(dto.getLongitudCentro());
        if (dto.getDificultad() != null) ruta.setDificultad(dto.getDificultad());
        if (dto.getTipoPaisaje() != null) ruta.setTipoPaisaje(dto.getTipoPaisaje());

        // Reemplazar trazado: orphanRemoval elimina las coordenadas viejas en BD
        ruta.getTrazadoGeografico().clear();
        ruta.getTrazadoGeografico().addAll(buildTrazado(dto.getTrazadoGeografico(), ruta));

        // Reemplazar POIs
        if (dto.getPuntosDeInteresIds() != null) {
            ruta.setPuntosDeInteres(fetchPois(dto.getPuntosDeInteresIds()));
        } else {
            ruta.getPuntosDeInteres().clear();
        }

        return mapToDetalleDto(rutaRepository.save(ruta));
    }

    private List<Coordenada> buildTrazado(List<CoordenadaDto> dtos, Ruta ruta) {
        List<Coordenada> trazado = new ArrayList<>();
        for (int i = 0; i < dtos.size(); i++) {
            Coordenada c = new Coordenada();
            c.setLatitud(dtos.get(i).getLatitud());
            c.setLongitud(dtos.get(i).getLongitud());
            c.setOrden(i);
            c.setRuta(ruta);
            trazado.add(c);
        }
        return trazado;
    }

    private List<PuntoInteres> fetchPois(List<Long> ids) {
        return ids.stream()
                .map(poiId -> puntoInteresRepository.findById(poiId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "El Punto de Interés con ID " + poiId + " no existe")))
                .collect(Collectors.toList());
    }

    private RutaDetalleResponseDto mapToDetalleDto(Ruta ruta) {
        RutaDetalleResponseDto dto = new RutaDetalleResponseDto();
        dto.setId(ruta.getId());
        dto.setNombre(ruta.getNombre());
        dto.setLatitudCentro(ruta.getLatitudCentro());
        dto.setLongitudCentro(ruta.getLongitudCentro());
        dto.setDificultad(ruta.getDificultad() != null ? ruta.getDificultad().name() : null);
        dto.setTipoPaisaje(ruta.getTipoPaisaje() != null ? ruta.getTipoPaisaje().name() : null);

        List<CoordenadaDto> trazadoDto = ruta.getTrazadoGeografico().stream()
                .map(c -> {
                    CoordenadaDto cd = new CoordenadaDto();
                    cd.setLatitud(c.getLatitud());
                    cd.setLongitud(c.getLongitud());
                    return cd;
                })
                .collect(Collectors.toList());
        dto.setTrazadoGeografico(trazadoDto);

        List<PuntoInteresResponseDto> poisDto = ruta.getPuntosDeInteres().stream()
                .map(p -> modelMapper.map(p, PuntoInteresResponseDto.class))
                .collect(Collectors.toList());
        dto.setPuntosDeInteres(poisDto);

        return dto;
    }
}
