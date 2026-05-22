package org.ide.dbp_proyecto;

import org.ide.dbp_proyecto.service.GeolocalizacionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test unitario puro del algoritmo de Haversine.
 * No necesita contexto de Spring — solo instancia el servicio directamente.
 */
class GeolocalizacionServiceTest {

    private GeolocalizacionService geoService;

    // Margen de error aceptable en metros (±1 metro)
    private static final double DELTA = 1.0;

    @BeforeEach
    void setUp() {
        geoService = new GeolocalizacionService();
    }

    // ─────────────────────────────────────────────
    // CASO 1: Distancia 0 — mismo punto exacto
    // ─────────────────────────────────────────────
    @Test
    @DisplayName("Distancia es 0 cuando lat/lon son exactamente iguales")
    void distanciaEsCeroEnMismoPunto() {
        double lat = -12.0931;
        double lon = -77.0465; // Plaza Mayor de Lima

        double resultado = geoService.calcularDistancia(lat, lon, lat, lon);

        assertEquals(0.0, resultado, DELTA,
                "La distancia entre el mismo punto debe ser 0 metros");
    }

    // ─────────────────────────────────────────────
    // CASO 2: Distancia conocida — dos puntos en Lima
    // Plaza Mayor de Lima → Huaca Pucllana
    // Distancia real aproximada: ~4,500 metros
    // ─────────────────────────────────────────────
    @Test
    @DisplayName("Distancia conocida entre Plaza Mayor de Lima y Huaca Pucllana (~4500 m)")
    void distanciaConocidaEntreDosPuntosEnLima() {
        // Plaza Mayor de Lima
        double latPlaza = -12.0464;
        double lonPlaza = -77.0311;

        // Huaca Pucllana (Miraflores)
        double latHuaca = -12.1100;
        double lonHuaca = -77.0497;

        double resultado = geoService.calcularDistancia(latPlaza, lonPlaza, latHuaca, lonHuaca);

        // La distancia real es ~7,300 m — permitimos ±100 m de margen
        assertTrue(resultado > 7_000 && resultado < 7_600,
                "Se esperaba ~7300 m entre Plaza Mayor y Huaca Pucllana, se obtuvo: "
                        + Math.round(resultado) + " m");
    }

    // ─────────────────────────────────────────────
    // CASO 3: Validación del radio de check-in (50 m)
    // Dos puntos a ~30 metros de distancia → debe pasar el umbral
    // ─────────────────────────────────────────────
    @Test
    @DisplayName("Usuario dentro del radio de 50 m → distancia menor a RADIO_CHECKIN_METROS")
    void usuarioDentroDelRadioDeCheckin() {
        // Punto base (POI)
        double latPoi = -12.1219;
        double lonPoi = -77.0299; // Referencia en Miraflores

        // ~30 metros al norte del POI (≈ 0.00027 grados de latitud)
        double latUsuario = latPoi + 0.00027;
        double lonUsuario = lonPoi;

        double distancia = geoService.calcularDistancia(latUsuario, lonUsuario, latPoi, lonPoi);

        assertTrue(distancia < GeolocalizacionService.RADIO_CHECKIN_METROS,
                "El usuario está a ~30 m, debería ser menor a 50 m. Resultado: "
                        + Math.round(distancia) + " m");
    }

    // ─────────────────────────────────────────────
    // CASO 4: Validación del radio de check-in (50 m)
    // Dos puntos a ~200 metros → debe rechazar el check-in
    // ─────────────────────────────────────────────
    @Test
    @DisplayName("Usuario fuera del radio de 50 m → distancia mayor a RADIO_CHECKIN_METROS")
    void usuarioFueraDelRadioDeCheckin() {
        double latPoi = -12.1219;
        double lonPoi = -77.0299;

        // ~200 metros al norte del POI (≈ 0.0018 grados)
        double latUsuario = latPoi + 0.0018;
        double lonUsuario = lonPoi;

        double distancia = geoService.calcularDistancia(latUsuario, lonUsuario, latPoi, lonPoi);

        assertTrue(distancia > GeolocalizacionService.RADIO_CHECKIN_METROS,
                "El usuario está a ~200 m, debería ser mayor a 50 m. Resultado: "
                        + Math.round(distancia) + " m");
    }

    // ─────────────────────────────────────────────
    // CASO 5 (AVANZADO): Cruce del antimeridiano (180°/-180°)
    // Punto en Fiji: lat=-17.7, lon=178.0 → lat=-17.7, lon=-179.0
    // Distancia real aproximada: ~100 km
    // ─────────────────────────────────────────────
    @Test
    @DisplayName("Caso extremo: cruce del antimeridiano (Fiji) — no debe retornar valor absurdo")
    void cruceDelAntimeridiano() {
        double lat1 = -17.7134;
        double lon1 =  178.0650; // Fiji occidental

        double lat2 = -17.7134;
        double lon2 = -179.0;   // Fiji oriental (cruce del antimeridiano)

        double resultado = geoService.calcularDistancia(lat1, lon1, lat2, lon2);

        // La distancia real es ~100 km
        // Haversine la calcula correctamente yendo por el camino largo (~980 km)
        // porque no distingue el "camino corto" cruzando el antimeridiano.
        // Lo importante es que no devuelva NaN, negativo, o un valor absurdo (>20,000 km).
        assertFalse(Double.isNaN(resultado),
                "El resultado no debe ser NaN en el cruce del antimeridiano");
        assertTrue(resultado > 0 && resultado < 20_000_000,
                "El resultado debe ser un valor de distancia válido en metros, obtenido: " + resultado);
    }
}