package org.ide.dbp_proyecto.service;

import lombok.RequiredArgsConstructor;
import org.ide.dbp_proyecto.service.CloudinaryService;
import org.ide.dbp_proyecto.repository.CategoriaRepository;
import org.ide.dbp_proyecto.repository.PuntoInteresRepository;
import org.ide.dbp_proyecto.repository.RutaRepository;
import org.ide.dbp_proyecto.entity.Categoria;
import org.ide.dbp_proyecto.entity.PuntoInteres;
import org.ide.dbp_proyecto.entity.Ruta;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PoiService {
    private final PuntoInteresRepository poiRepository;
    private final CategoriaRepository categoriaRepository;
    private final RutaRepository rutaRepository;
    private final CloudinaryService cloudinaryService;

    public PuntoInteres createPoi(String nombre, String descripcion, Double latitud, Double longitud, Long categoriaId, MultipartFile imagen) throws IOException {
        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        String imageUrl = cloudinaryService.uploadImage(imagen);

        PuntoInteres poi = new PuntoInteres();
        poi.setNombre(nombre);
        poi.setDescripcion(descripcion);
        poi.setLatitud(latitud);
        poi.setLongitud(longitud);
        poi.setCategoria(categoria);
        poi.setUrlImagen(imageUrl);

        return poiRepository.save(poi);
    }

    public List<PuntoInteres> getPois(Long categoriaId) {
        if (categoriaId != null) {
            return poiRepository.findByCategoriaId(categoriaId);
        }
        return poiRepository.findAll();
    }

    public void asociarPoiARuta(Long idRuta, Long idPoi) {
        Ruta ruta = rutaRepository.findById(idRuta).orElseThrow(() -> new RuntimeException("Ruta no encontrada"));
        PuntoInteres poi = poiRepository.findById(idPoi).orElseThrow(() -> new RuntimeException("POI no encontrado"));

        if(!ruta.getPuntosDeInteres().contains(poi)){
            ruta.getPuntosDeInteres().add(poi);
            rutaRepository.save(ruta);
        }
    }

    public List<PuntoInteres> getPoisByRuta(Long idRuta) {
        Ruta ruta = rutaRepository.findById(idRuta).orElseThrow(() -> new RuntimeException("Ruta no encontrada"));
        return ruta.getPuntosDeInteres();
    }
}
