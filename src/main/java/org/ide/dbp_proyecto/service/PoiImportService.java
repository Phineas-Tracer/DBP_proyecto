package org.ide.dbp_proyecto.service;

import org.ide.dbp_proyecto.service.GooglePlacesService;
import org.ide.dbp_proyecto.dto.SugerenciaPoiDTO;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.ide.dbp_proyecto.dto.ImportacionPoiResponseDto;
import org.ide.dbp_proyecto.dto.google.GooglePlaceDto;
import org.ide.dbp_proyecto.entity.Categoria;
import org.ide.dbp_proyecto.entity.PuntoInteres;
import org.ide.dbp_proyecto.entity.Ruta;
import org.ide.dbp_proyecto.exception.ResourceNotFoundException;
import org.ide.dbp_proyecto.Repository.CategoriaRepository;
import org.ide.dbp_proyecto.Repository.PuntoInteresRepository;
import org.ide.dbp_proyecto.Repository.RutaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class PoiImportService {

    private static final Logger log = LoggerFactory.getLogger(PoiImportService.class);

    private final PuntoInteresRepository poiRepository;
    private final CategoriaRepository categoriaRepository;
    private final RutaRepository rutaRepository;

    private final GooglePlacesService googlePlacesService;

    // Mapeo de tipos Google → categorías locales
    private static final Map<String, String> TIPO_GOOGLE_A_CATEGORIA = Map.ofEntries(
            // Gastronomía
            Map.entry("restaurant", "Gastronomía"),
            Map.entry("peruvian_restaurant", "Gastronomía"),
            Map.entry("seafood_restaurant", "Gastronomía"),
            Map.entry("fast_food_restaurant", "Gastronomía"),
            Map.entry("cafe", "Gastronomía"),
            Map.entry("cafeteria", "Gastronomía"),
            Map.entry("bakery", "Gastronomía"),
            Map.entry("bar", "Gastronomía"),
            Map.entry("bar_and_grill", "Gastronomía"),

            // Hospedaje
            Map.entry("hotel", "Hospedaje"),
            Map.entry("hostel", "Hospedaje"),
            Map.entry("lodging", "Hospedaje"),
            Map.entry("motel", "Hospedaje"),

            // Zonas turísticas
            Map.entry("tourist_attraction", "Zona Turística"),
            Map.entry("museum", "Zona Turística"),
            Map.entry("park", "Zona Turística"),
            Map.entry("natural_feature", "Zona Turística"),
            Map.entry("campground", "Zona Turística"),
            Map.entry("amusement_park", "Zona Turística"),

            // Artesanía / Comercio local
            Map.entry("store", "Artesanía"),
            Map.entry("market", "Artesanía"),
            Map.entry("shopping_mall", "Artesanía"),
            Map.entry("clothing_store", "Artesanía")
    );

    private static final double RADIO_DEFAULT_METROS = 2000.0;

    private static final List<String> TIPOS_DEFAULT = List.of(
            "restaurant",
            "hotel",
            "tourist_attraction",
            "store"
    );

    // Importa POIs desde Google Places y los asocia a una Ruta

    @Transactional
    public ImportacionPoiResponseDto importarPoisParaRuta(
            Long rutaId,
            Double radioMetros,
            List<String> tipos) {

        // 1) Verificar que la Ruta existe
        Ruta ruta = rutaRepository.findById(rutaId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ruta no encontrada con id: " + rutaId));

        // 2) Obtener las coordenadas del centro de la Ruta
        double latCentro = ruta.getLatitudCentro();
        double lonCentro = ruta.getLongitudCentro();

        // 3) Parámetros de búsqueda con defaults
        double radio = (radioMetros != null) ? radioMetros : RADIO_DEFAULT_METROS;
        List<String> tiposBusqueda = (tipos != null && !tipos.isEmpty()) ? tipos : TIPOS_DEFAULT;

        log.info("Iniciando importación para ruta '{}' (id={}), centro=({},{}), radio={}m",
                ruta.getNombre(), rutaId, latCentro, lonCentro, radio);

        // 4) Llamar a Google Places
        List<GooglePlaceDto> lugaresGoogle = googlePlacesService.buscarLugaresCercanos(
                latCentro, lonCentro, radio, tiposBusqueda);

        // 5) Procesar cada lugar
        int importados = 0;
        int duplicados = 0;
        List<String> nombresImportados = new ArrayList<>();

        for (GooglePlaceDto lugar : lugaresGoogle) {

            // 5a) Verificar duplicado por googlePlaceId
            if (poiRepository.existsByGooglePlaceId(lugar.id())) {
                log.debug("Duplicado ignorado: {} ({})", lugar.displayName().text(), lugar.id());

                // Aunque sea duplicado, lo asociamos a la ruta si aún no está
                poiRepository.findByGooglePlaceId(lugar.id()).ifPresent(poi -> {
                    if (!ruta.getPuntosDeInteres().contains(poi)) {
                        ruta.getPuntosDeInteres().add(poi);
                    }
                });

                duplicados++;
                continue;
            }

            // Mapear tipo de Google → categoría local
            Categoria categoria = resolverCategoria(lugar);

            // Crear la entidad PuntoInteres
            PuntoInteres poi = new PuntoInteres();
            poi.setNombre(lugar.displayName().text());
            poi.setDescripcion(lugar.formattedAddress());
            poi.setLatitud(lugar.location().latitude());
            poi.setLongitud(lugar.location().longitude());
            poi.setDireccion(lugar.formattedAddress());
            poi.setGooglePlaceId(lugar.id());
            poi.setCategoria(categoria);
            // urlImagen queda null — se puede enriquecer después con Cloudinary

            // Persistir en BD
            PuntoInteres poiGuardado = poiRepository.save(poi);

            // Asociar a la Ruta
            ruta.getPuntosDeInteres().add(poiGuardado);

            nombresImportados.add(poiGuardado.getNombre());
            importados++;

            log.debug("Importado: {} → categoría '{}'",
                    poiGuardado.getNombre(), categoria.getNombre());
        }

        // 6) Guardar la Ruta con todas las asociaciones nuevas
        rutaRepository.save(ruta);

        log.info("Importación completada: {} nuevos, {} duplicados ignorados",
                importados, duplicados);

        return new ImportacionPoiResponseDto(
                rutaId,
                lugaresGoogle.size(),
                importados,
                duplicados,
                nombresImportados
        );
    }

    public List<SugerenciaPoiDTO> buscarSugerenciasParaRuta(
            Long rutaId,
            Double radioMetros,
            List<String> tipos) {

        Ruta ruta = rutaRepository.findById(rutaId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ruta no encontrada con id: " + rutaId));

        double radio = (radioMetros != null) ? radioMetros : RADIO_DEFAULT_METROS;
        List<String> tiposBusqueda = (tipos != null && !tipos.isEmpty()) ? tipos : TIPOS_DEFAULT;

        log.info("Buscando sugerencias en vivo para ruta '{}' (id={}), radio={}m",
                ruta.getNombre(), rutaId, radio);

        List<GooglePlaceDto> lugaresGoogle = googlePlacesService.buscarLugaresCercanos(
                ruta.getLatitudCentro(),
                ruta.getLongitudCentro(),
                radio,
                tiposBusqueda
        );

        // Mapear cada lugar de Google a un DTO público (sin persistir)
        return lugaresGoogle.stream()
                .map(this::mapearASugerencia)
                .toList();
    }

    private SugerenciaPoiDTO mapearASugerencia(GooglePlaceDto lugar) {
        String nombreCategoria = "Otro";
        if (lugar.types() != null) {
            for (String tipo : lugar.types()) {
                if (TIPO_GOOGLE_A_CATEGORIA.containsKey(tipo)) {
                    nombreCategoria = TIPO_GOOGLE_A_CATEGORIA.get(tipo);
                    break;
                }
            }
        }

        return new SugerenciaPoiDTO(
                lugar.id(),
                lugar.displayName().text(),
                lugar.formattedAddress(),
                lugar.location().latitude(),
                lugar.location().longitude(),
                nombreCategoria,
                lugar.primaryType()
        );
    }

    @Transactional
    public PuntoInteres persistirPoiDesdeGoogle(SugerenciaPoiDTO sugerencia) {
        // Si ya existe, lo devolvemos sin duplicar
        return poiRepository.findByGooglePlaceId(sugerencia.googlePlaceId())
                .orElseGet(() -> {
                    log.info("Persistiendo nuevo POI desde sugerencia: {}", sugerencia.nombre());

                    // Resolver la categoría (la crea si no existe)
                    Categoria categoria = categoriaRepository.findByNombre(sugerencia.categoria())
                            .orElseGet(() -> {
                                Categoria nueva = new Categoria();
                                nueva.setNombre(sugerencia.categoria());
                                return categoriaRepository.save(nueva);
                            });

                    PuntoInteres poi = new PuntoInteres();
                    poi.setNombre(sugerencia.nombre());
                    poi.setDescripcion(sugerencia.direccion());
                    poi.setLatitud(sugerencia.latitud());
                    poi.setLongitud(sugerencia.longitud());
                    poi.setDireccion(sugerencia.direccion());
                    poi.setGooglePlaceId(sugerencia.googlePlaceId());
                    poi.setCategoria(categoria);

                    return poiRepository.save(poi);
                });
    }

    //-------------------------------------------------------------------------------
    private Categoria resolverCategoria(GooglePlaceDto lugar) {
        String nombreCategoria = "Otro"; // default

        if (lugar.types() != null) {
            for (String tipo : lugar.types()) {
                if (TIPO_GOOGLE_A_CATEGORIA.containsKey(tipo)) {
                    nombreCategoria = TIPO_GOOGLE_A_CATEGORIA.get(tipo);
                    break;
                }
            }
        }

        // Busca la categoría en BD, o la crea si no existe
        String finalNombreCategoria = nombreCategoria;
        return categoriaRepository.findByNombre(nombreCategoria)
                .orElseGet(() -> {
                    Categoria nueva = new Categoria();
                    nueva.setNombre(finalNombreCategoria);
                    log.info("Creando nueva categoría: '{}'", finalNombreCategoria);
                    return categoriaRepository.save(nueva);
                });
    }
}
