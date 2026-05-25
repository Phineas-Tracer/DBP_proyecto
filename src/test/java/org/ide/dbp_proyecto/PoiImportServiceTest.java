package org.ide.dbp_proyecto;

import org.ide.dbp_proyecto.dto.ImportacionPoiResponseDto;
import org.ide.dbp_proyecto.dto.google.DisplayNameDto;
import org.ide.dbp_proyecto.dto.google.GooglePlaceDto;
import org.ide.dbp_proyecto.dto.google.LocationDto;
import org.ide.dbp_proyecto.entity.Categoria;
import org.ide.dbp_proyecto.entity.PuntoInteres;
import org.ide.dbp_proyecto.entity.Ruta;
import org.ide.dbp_proyecto.repository.CategoriaRepository;
import org.ide.dbp_proyecto.repository.PuntoInteresRepository;
import org.ide.dbp_proyecto.repository.RutaRepository;
import org.ide.dbp_proyecto.service.GooglePlacesService;
import org.ide.dbp_proyecto.service.PoiImportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ide.dbp_proyecto.dto.SugerenciaPoiDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios del PoiImportService.
 *
 * No llaman a Google Places real ni a la BD real.
 * Usan Mockito para simular las respuestas de los repositorios y del servicio externo.
 */
@ExtendWith(MockitoExtension.class)
class PoiImportServiceTest {

    @Mock private PuntoInteresRepository poiRepository;
    @Mock private CategoriaRepository categoriaRepository;
    @Mock private RutaRepository rutaRepository;
    @Mock private GooglePlacesService googlePlacesService;

    @InjectMocks
    private PoiImportService poiImportService;

    private Ruta rutaMock;
    private Categoria categoriaMock;

    @BeforeEach
    void setUp() {
        rutaMock = new Ruta();
        rutaMock.setId(1L);
        rutaMock.setNombre("Marcahuasi");
        rutaMock.setLatitudCentro(-11.7811);
        rutaMock.setLongitudCentro(-76.5708);
        rutaMock.setPuntosDeInteres(new ArrayList<>());

        categoriaMock = new Categoria();
        categoriaMock.setId(1L);
        categoriaMock.setNombre("Gastronomía");
    }

    // caso 1: Importación exitosa de lugares nuevos

    @Test
    @DisplayName("Importa correctamente lugares nuevos y los asocia a la ruta")
    void importaPoisNuevosCorrectamente() {
        // Arrange
        List<GooglePlaceDto> lugaresGoogle = List.of(
                crearLugarMock("place_001", "Restaurante La Mar", -12.1013, -77.0365, "restaurant"),
                crearLugarMock("place_002", "Hotel Casa Andina", -12.1219, -77.0266, "hotel")
        );

        when(rutaRepository.findById(1L)).thenReturn(Optional.of(rutaMock));
        when(googlePlacesService.buscarLugaresCercanos(anyDouble(), anyDouble(), anyDouble(), any()))
                .thenReturn(lugaresGoogle);
        when(poiRepository.existsByGooglePlaceId(anyString())).thenReturn(false);
        when(categoriaRepository.findByNombre("Gastronomía")).thenReturn(Optional.of(categoriaMock));
        when(categoriaRepository.findByNombre("Hospedaje")).thenReturn(Optional.of(
                crearCategoriaMock(2L, "Hospedaje")));
        when(poiRepository.save(any(PuntoInteres.class))).thenAnswer(inv -> inv.getArgument(0));
        when(rutaRepository.save(any(Ruta.class))).thenReturn(rutaMock);

        // Act
        ImportacionPoiResponseDto resultado = poiImportService.importarPoisParaRuta(1L, 2000.0, null);

        // Assert
        assertEquals(2, resultado.totalEncontrados());
        assertEquals(2, resultado.importados());
        assertEquals(0, resultado.duplicadosIgnorados());
        assertTrue(resultado.nombresImportados().contains("Restaurante La Mar"));
        assertTrue(resultado.nombresImportados().contains("Hotel Casa Andina"));

        // Verificar que se guardaron exactamente 2 POIs en BD
        verify(poiRepository, times(2)).save(any(PuntoInteres.class));
        verify(rutaRepository, times(1)).save(rutaMock);
    }

    // caso 2: Duplicados ignorados correctamente

    @Test
    @DisplayName("Ignora POIs que ya existen en BD (por googlePlaceId)")
    void ignoraDuplicadosCorrectamente() {
        // Arrange — Google devuelve 2 lugares, pero ambos ya existen en BD
        List<GooglePlaceDto> lugaresGoogle = List.of(
                crearLugarMock("place_001", "Restaurante La Mar", -12.1013, -77.0365, "restaurant"),
                crearLugarMock("place_002", "Hotel Casa Andina", -12.1219, -77.0266, "hotel")
        );

        PuntoInteres poiExistente = new PuntoInteres();
        poiExistente.setNombre("Restaurante La Mar");

        when(rutaRepository.findById(1L)).thenReturn(Optional.of(rutaMock));
        when(googlePlacesService.buscarLugaresCercanos(anyDouble(), anyDouble(), anyDouble(), any()))
                .thenReturn(lugaresGoogle);
        when(poiRepository.existsByGooglePlaceId(anyString())).thenReturn(true);
        when(poiRepository.findByGooglePlaceId(anyString()))
                .thenReturn(Optional.of(poiExistente));
        when(rutaRepository.save(any(Ruta.class))).thenReturn(rutaMock);

        // Act
        ImportacionPoiResponseDto resultado = poiImportService.importarPoisParaRuta(1L, 2000.0, null);

        // Assert
        assertEquals(2, resultado.totalEncontrados());
        assertEquals(0, resultado.importados());
        assertEquals(2, resultado.duplicadosIgnorados());

        // Nunca debe guardarse un POI nuevo en BD
        verify(poiRepository, never()).save(any(PuntoInteres.class));
    }


    // caso 3: Google Places no devuelve resultados
    @Test
    @DisplayName("Maneja correctamente cuando Google Places no devuelve resultados")
    void manejaListaVaciaDeGoogle() {
        // Arrange
        when(rutaRepository.findById(1L)).thenReturn(Optional.of(rutaMock));
        when(googlePlacesService.buscarLugaresCercanos(anyDouble(), anyDouble(), anyDouble(), any()))
                .thenReturn(List.of());
        when(rutaRepository.save(any(Ruta.class))).thenReturn(rutaMock);

        // Act
        ImportacionPoiResponseDto resultado = poiImportService.importarPoisParaRuta(1L, 2000.0, null);

        // Assert
        assertEquals(0, resultado.totalEncontrados());
        assertEquals(0, resultado.importados());
        assertEquals(0, resultado.duplicadosIgnorados());
        assertTrue(resultado.nombresImportados().isEmpty());

        // No debe intentar guardar nada
        verify(poiRepository, never()).save(any(PuntoInteres.class));
    }

    // caso 4: Ruta no encontrada lanza excepción
    @Test
    @DisplayName("Lanza excepción cuando la ruta no existe en BD")
    void lanzaExcepcionSiRutaNoExiste() {
        // Arrange
        when(rutaRepository.findById(99L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(
                org.ide.dbp_proyecto.exception.ResourceNotFoundException.class,
                () -> poiImportService.importarPoisParaRuta(99L, 2000.0, null),
                "Debería lanzar ResourceNotFoundException si la ruta no existe"
        );

        // Nunca debe llegar a llamar a Google Places
        verify(googlePlacesService, never()).buscarLugaresCercanos(anyDouble(), anyDouble(), anyDouble(), any());
    }

    // caso 5: Mix de nuevos y duplicados

    @Test
    @DisplayName("Maneja correctamente una mezcla de POIs nuevos y duplicados")
    void manejaCorrectamenteMixNuevosYDuplicados() {
        // Arrange — 3 lugares, 1 ya existe
        List<GooglePlaceDto> lugaresGoogle = List.of(
                crearLugarMock("place_001", "Restaurante La Mar", -12.1013, -77.0365, "restaurant"),
                crearLugarMock("place_002", "Hotel Casa Andina", -12.1219, -77.0266, "hotel"),
                crearLugarMock("place_003", "Maido", -12.1254, -77.0306, "restaurant")
        );

        PuntoInteres poiExistente = new PuntoInteres();
        poiExistente.setNombre("Maido");

        when(rutaRepository.findById(1L)).thenReturn(Optional.of(rutaMock));
        when(googlePlacesService.buscarLugaresCercanos(anyDouble(), anyDouble(), anyDouble(), any()))
                .thenReturn(lugaresGoogle);
        // place_001 y place_002 son nuevos, place_003 ya existe
        when(poiRepository.existsByGooglePlaceId("place_001")).thenReturn(false);
        when(poiRepository.existsByGooglePlaceId("place_002")).thenReturn(false);
        when(poiRepository.existsByGooglePlaceId("place_003")).thenReturn(true);
        when(poiRepository.findByGooglePlaceId("place_003")).thenReturn(Optional.of(poiExistente));
        when(categoriaRepository.findByNombre(anyString())).thenReturn(Optional.of(categoriaMock));
        when(poiRepository.save(any(PuntoInteres.class))).thenAnswer(inv -> inv.getArgument(0));
        when(rutaRepository.save(any(Ruta.class))).thenReturn(rutaMock);

        // Act
        ImportacionPoiResponseDto resultado = poiImportService.importarPoisParaRuta(1L, 2000.0, null);

        // Assert
        assertEquals(3, resultado.totalEncontrados());
        assertEquals(2, resultado.importados());
        assertEquals(1, resultado.duplicadosIgnorados());
        verify(poiRepository, times(2)).save(any(PuntoInteres.class));
    }

    //  TESTS DE buscarSugerenciasParaRuta (modo on-demand sin persistir)

    @Test
    @DisplayName("buscarSugerencias: devuelve sugerencias sin persistir nada en BD")
    void buscarSugerenciasNoPersisteEnBD() {
        // Arrange
        List<GooglePlaceDto> lugaresGoogle = List.of(
                crearLugarMock("place_001", "Restaurante La Mar", -12.1013, -77.0365, "restaurant"),
                crearLugarMock("place_002", "Hotel Casa Andina", -12.1219, -77.0266, "hotel")
        );

        when(rutaRepository.findById(1L)).thenReturn(Optional.of(rutaMock));
        when(googlePlacesService.buscarLugaresCercanos(anyDouble(), anyDouble(), anyDouble(), any()))
                .thenReturn(lugaresGoogle);

        // Act
        List<SugerenciaPoiDTO> sugerencias =
                poiImportService.buscarSugerenciasParaRuta(1L, 2000.0, null);

        // Assert
        assertEquals(2, sugerencias.size());
        assertEquals("Restaurante La Mar", sugerencias.get(0).nombre());
        assertEquals("Hotel Casa Andina", sugerencias.get(1).nombre());

        // CRÍTICO: jamás se debe persistir nada en este modo
        verify(poiRepository, never()).save(any(PuntoInteres.class));
        verify(categoriaRepository, never()).save(any(Categoria.class));
        verify(rutaRepository, never()).save(any(Ruta.class));
    }

    @Test
    @DisplayName("buscarSugerencias: mapea correctamente la categoría según el tipo de Google")
    void buscarSugerenciasMapeaCategoriaCorrectamente() {
        // Arrange
        List<GooglePlaceDto> lugaresGoogle = List.of(
                crearLugarMock("place_001", "Restaurante X", -12.0, -77.0, "restaurant"),
                crearLugarMock("place_002", "Hotel Y", -12.0, -77.0, "hotel"),
                crearLugarMock("place_003", "Museo Z", -12.0, -77.0, "museum")
        );

        when(rutaRepository.findById(1L)).thenReturn(Optional.of(rutaMock));
        when(googlePlacesService.buscarLugaresCercanos(anyDouble(), anyDouble(), anyDouble(), any()))
                .thenReturn(lugaresGoogle);

        // Act
        List<SugerenciaPoiDTO> sugerencias =
                poiImportService.buscarSugerenciasParaRuta(1L, 2000.0, null);

        // Assert
        assertEquals("Gastronomía", sugerencias.get(0).categoria());
        assertEquals("Hospedaje", sugerencias.get(1).categoria());
        assertEquals("Zona Turística", sugerencias.get(2).categoria());
    }

    @Test
    @DisplayName("buscarSugerencias: lanza excepción si la ruta no existe")
    void buscarSugerenciasLanzaExcepcionSiRutaNoExiste() {
        when(rutaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                org.ide.dbp_proyecto.exception.ResourceNotFoundException.class,
                () -> poiImportService.buscarSugerenciasParaRuta(99L, 2000.0, null)
        );

        verify(googlePlacesService, never()).buscarLugaresCercanos(anyDouble(), anyDouble(), anyDouble(), any());
    }

    //  TESTS DE persistirPoiDesdeGoogle

    @Test
    @DisplayName("persistirPoi: si el POI ya existe en BD, lo devuelve sin duplicar")
    void persistirPoiDevuelveExistenteSinDuplicar() {
        // Arrange
        PuntoInteres poiExistente = new PuntoInteres();
        poiExistente.setId(42L);
        poiExistente.setNombre("Restaurante existente");
        poiExistente.setGooglePlaceId("place_existente");

        var sugerencia = new SugerenciaPoiDTO(
                "place_existente",
                "Restaurante existente",
                "Av. X 123",
                -12.0, -77.0,
                "Gastronomía",
                "restaurant"
        );

        when(poiRepository.findByGooglePlaceId("place_existente"))
                .thenReturn(Optional.of(poiExistente));

        // Act
        PuntoInteres resultado = poiImportService.persistirPoiDesdeGoogle(sugerencia);

        // Assert
        assertEquals(42L, resultado.getId());
        assertEquals("Restaurante existente", resultado.getNombre());

        // No debe guardar nada nuevo
        verify(poiRepository, never()).save(any(PuntoInteres.class));
    }

    @Test
    @DisplayName("persistirPoi: si el POI NO existe, lo crea con los datos de la sugerencia")
    void persistirPoiCreaNuevoSiNoExiste() {
        // Arrange
        var sugerencia = new SugerenciaPoiDTO(
                "place_nuevo",
                "Hotel Nuevo",
                "Av. Y 456",
                -12.5, -77.5,
                "Hospedaje",
                "hotel"
        );

        Categoria categoriaHospedaje = crearCategoriaMock(2L, "Hospedaje");

        when(poiRepository.findByGooglePlaceId("place_nuevo")).thenReturn(Optional.empty());
        when(categoriaRepository.findByNombre("Hospedaje")).thenReturn(Optional.of(categoriaHospedaje));
        when(poiRepository.save(any(PuntoInteres.class))).thenAnswer(inv -> {
            PuntoInteres p = inv.getArgument(0);
            p.setId(100L);
            return p;
        });

        // Act
        PuntoInteres resultado = poiImportService.persistirPoiDesdeGoogle(sugerencia);

        // Assert
        assertNotNull(resultado);
        assertEquals("Hotel Nuevo", resultado.getNombre());
        assertEquals("place_nuevo", resultado.getGooglePlaceId());
        assertEquals(-12.5, resultado.getLatitud());
        assertEquals(-77.5, resultado.getLongitud());
        assertEquals("Hospedaje", resultado.getCategoria().getNombre());

        verify(poiRepository, times(1)).save(any(PuntoInteres.class));
    }

    @Test
    @DisplayName("persistirPoi: crea la categoría si no existe en BD")
    void persistirPoiCreaCategoriaSiNoExiste() {
        // Arrange
        var sugerencia = new SugerenciaPoiDTO(
                "place_nuevo",
                "Lugar exótico",
                "Av. Z 789",
                -12.0, -77.0,
                "CategoriaInexistente",  // categoría que NO está en BD
                "unknown_type"
        );

        when(poiRepository.findByGooglePlaceId("place_nuevo")).thenReturn(Optional.empty());
        when(categoriaRepository.findByNombre("CategoriaInexistente")).thenReturn(Optional.empty());
        when(categoriaRepository.save(any(Categoria.class))).thenAnswer(inv -> {
            Categoria c = inv.getArgument(0);
            c.setId(99L);
            return c;
        });
        when(poiRepository.save(any(PuntoInteres.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        PuntoInteres resultado = poiImportService.persistirPoiDesdeGoogle(sugerencia);

        // Assert
        assertEquals("CategoriaInexistente", resultado.getCategoria().getNombre());

        // Se debe crear la categoría nueva
        verify(categoriaRepository, times(1)).save(any(Categoria.class));
    }

    // Helpers

    private GooglePlaceDto crearLugarMock(String id, String nombre, double lat, double lon, String tipo) {
        return new GooglePlaceDto(
                id,
                new DisplayNameDto(nombre, "es"),
                "Dirección de " + nombre,
                new LocationDto(lat, lon),
                List.of(tipo, "point_of_interest", "establishment"),
                tipo
        );
    }

    private Categoria crearCategoriaMock(Long id, String nombre) {
        Categoria c = new Categoria();
        c.setId(id);
        c.setNombre(nombre);
        return c;
    }
}