package org.ide.dbp_proyecto.service;

import org.ide.dbp_proyecto.event.CheckinRealizadoEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import org.ide.dbp_proyecto.service.GeolocalizacionService;
import org.ide.dbp_proyecto.exception.CheckinFueraDeRangoException;
import org.ide.dbp_proyecto.repository.PuntoInteresRepository;
import org.ide.dbp_proyecto.dto.CheckinRequestDTO;
import org.ide.dbp_proyecto.dto.CheckinResponseDTO;
import org.ide.dbp_proyecto.dto.LugarColeccionadoDTO;
import org.ide.dbp_proyecto.entity.LugarColeccionado;
import org.ide.dbp_proyecto.entity.PuntoInteres;
import org.ide.dbp_proyecto.entity.User;
import org.ide.dbp_proyecto.exception.ConflictException;
import org.ide.dbp_proyecto.exception.ResourceNotFoundException;
import org.ide.dbp_proyecto.repository.LugarColeccionadoRepository;
import org.ide.dbp_proyecto.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlbumService {

    private final LugarColeccionadoRepository lugarColeccionadoRepository;
    private final GeolocalizacionService geolocalizacionService;
    private final UserRepository userRepository;
    private final PuntoInteresRepository poiRepository;
    private final ModelMapper modelMapper;
    private final ApplicationEventPublisher eventPublisher;

    public AlbumService(ModelMapper modelMapper, LugarColeccionadoRepository lugarColeccionadoRepository,
                        PuntoInteresRepository poiRepository, GeolocalizacionService geolocalizacionService,
                        UserRepository userRepository, ApplicationEventPublisher eventPublisher) {
        this.modelMapper = modelMapper;
        this.lugarColeccionadoRepository = lugarColeccionadoRepository;
        this.poiRepository = poiRepository;
        this.geolocalizacionService = geolocalizacionService;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public CheckinResponseDTO realizarCheckin(CheckinRequestDTO dto, String emailUsuario) {

        User usuario = userRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        PuntoInteres poi = poiRepository.findById(dto.poiId())
                .orElseThrow(()-> new ResourceNotFoundException("Punto de Interes no encontrado"));

        if (lugarColeccionadoRepository.existsByUsuarioAndPuntoDeInteres(usuario, poi)){
            throw new ConflictException("Ya tienes este lugar coleccionado");
        }

        double distancia = geolocalizacionService.calcularDistancia(dto.latitudUsuario(), dto.longitudUsuario(), poi.getLatitud(), poi.getLongitud());
        if (distancia > GeolocalizacionService.RADIO_CHECKIN_METROS){
            throw new CheckinFueraDeRangoException("Estás a " + Math.round(distancia) + "metros. Debes estar a menos de 50 metros para coleccionar este lugar.");
        }

        LugarColeccionado coleccion = new LugarColeccionado();
        coleccion.setUsuario(usuario);
        coleccion.setPuntoDeInteres(poi);
        coleccion.setFecha(LocalDateTime.now());
        coleccion.setLatitudCheckin(dto.latitudUsuario());
        coleccion.setLongitudCheckin(dto.longitudUsuario());

        lugarColeccionadoRepository.save(coleccion);

        // Publicar evento — listeners async enviarán email, evaluarán retos, etc.
        eventPublisher.publishEvent(new CheckinRealizadoEvent(this, coleccion));

        List<String> recompensas = new ArrayList<>();

        return new CheckinResponseDTO(
                "Check-in exitoso",
                poi.getNombre(),
                coleccion.getFecha(),
                distancia,
                recompensas
        );
    }

    public List<LugarColeccionadoDTO> obtenerMiColeccion(String emailUsuario) {
        User usuario = userRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        return lugarColeccionadoRepository.findByUsuario(usuario).stream().map(lugar -> {
            return new LugarColeccionadoDTO(
                    lugar.getPuntoDeInteres().getId(),
                    lugar.getPuntoDeInteres().getNombre(),
                    lugar.getPuntoDeInteres().getCategoria().getNombre(),
                    lugar.getPuntoDeInteres().getUrlImagen(),
                    lugar.getFecha(),
                    lugar.getLatitudCheckin(),
                    lugar.getLongitudCheckin()
            );
        }).collect(Collectors.toList());
    }
}