package org.ide.dbp_proyecto.service;

import org.ide.dbp_proyecto.Temporales.PuntoDeInteres;
import org.ide.dbp_proyecto.Temporales.PuntoDeInteresRepository;
import org.ide.dbp_proyecto.Temporales.Usuario;
import org.ide.dbp_proyecto.dto.CheckinRequestDTO;
import org.ide.dbp_proyecto.dto.CheckinResponseDTO;
import org.ide.dbp_proyecto.exception.ConflictException;
import org.ide.dbp_proyecto.exception.ResourceNotFoundException;
import org.ide.dbp_proyecto.repository.LugarColeccionadoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class AlbumService {

    private final LugarColeccionadoRepository lugarColeccionadoRepository;
    private final GeolocalizacionService geolocalizacionService;
    private final PuntoDeInteresRepository poiRepository;
    private final ModelMapper modelMapper;

    public AlbumService(ModelMapper modelMapper,  LugarColeccionadoRepository lugarColeccionadoRepository, PuntoDeInteresRepository poiRepository,
                        GeolocalizacionService geolocalizacionService) {
        this.modelMapper = modelMapper;
        this.lugarColeccionadoRepository = lugarColeccionadoRepository;
        this.poiRepository = poiRepository;
        this.geolocalizacionService = geolocalizacionService;
    }

    public CheckinResponseDTO realizarCheckin(CheckinRequestDTO dto, Usuario usuario) {
        PuntoDeInteres poi = poiRepository.findById(dto.poiId()).
                orElseThrow(()-> new ResourceNotFoundException("Punto de Interes no encontrado"));

        if (lugarColeccionadoRepository.existsByUsuarioAndPuntoDeInteres(usuario, poi)){
            throw new ConflictException("Ya tienes este lugar coleccionado");
        }

        double distancia = geolocalizacionService.calcularDistancia(dto.latitudUsuario(), dto.longitudUsuario(), poi.getLatitud(), poi.getLongitud());


    }
}
