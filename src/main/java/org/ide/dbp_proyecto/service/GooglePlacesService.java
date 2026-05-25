package org.ide.dbp_proyecto.Service;

import org.ide.dbp_proyecto.DTO.google.*;
import org.ide.dbp_proyecto.exception.GooglePlacesException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.ide.dbp_proyecto.DTO.google.GooglePlaceDto;
import org.ide.dbp_proyecto.DTO.google.NearbySearchRequestDto;
import org.ide.dbp_proyecto.DTO.google.LocationRestrictionDto;
import org.ide.dbp_proyecto.DTO.google.LocationDto;
import org.ide.dbp_proyecto.DTO.google.CircleDto;
import org.ide.dbp_proyecto.DTO.google.GooglePlacesResponseDto;


import org.slf4j.Logger;

import java.util.Collections;
import java.util.List;

@Service
public class GooglePlacesService {
    private static final Logger log = LoggerFactory.getLogger(GooglePlacesService.class);

    // Campos que pedimos a Google (FieldMask)
    private static final String FIELD_MASK = String.join(",",
            "places.id",
            "places.displayName",
            "places.formattedAddress",
            "places.location",
            "places.types",
            "places.primaryType"
    );

    // Límite máximo permitido por Google en una sola llamada
    private static final int MAX_RESULTS = 20;

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String baseUrl;

    public GooglePlacesService(RestTemplate restTemplate, @Value("${google.places.api-key}") String apiKey,
                               @Value("${google.places.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
    }


    public List<GooglePlaceDto> buscarLugaresCercanos(double latitud, double longitud, double radioMetros, List<String> tipos) {

        // 1) Construir el body del request usando los DTOs de la Fase 3
    NearbySearchRequestDto requestBody = new NearbySearchRequestDto( tipos, MAX_RESULTS,
            new LocationRestrictionDto(
                    new CircleDto(new LocationDto(latitud, longitud), radioMetros
                    )
            )
    );

    // 2) Construir los headers requeridos por Google Places (New)
    HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Goog-Api-Key", apiKey);
        headers.set("X-Goog-FieldMask", FIELD_MASK);

    // 3) Encapsular body + headers en una HttpEntity
    HttpEntity<NearbySearchRequestDto> entity = new HttpEntity<>(requestBody, headers);

    String url = baseUrl + "/places:searchNearby";

    log.info("Consultando Google Places: lat={}, lon={}, radio={}m, tipos={}",
            latitud, longitud, radioMetros, tipos);

        try {
            // 4) Hacer el POST y parsear la respuesta
            ResponseEntity<GooglePlacesResponseDto> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    GooglePlacesResponseDto.class
            );

            GooglePlacesResponseDto body = response.getBody();

            // 5) Si Google no devolvió "places", retornamos lista vacía (no es error)
            if (body == null || body.places() == null) {
                log.info("Google Places no devolvió resultados");
                return Collections.emptyList();
            }

            log.info("Google Places devolvió {} resultados", body.places().size());
            return body.places();

        } catch (RestClientException ex) {
            log.error("Error al consultar Google Places: {}", ex.getMessage());
            throw new GooglePlacesException(
                    "No se pudo consultar Google Places. Verifica la API Key y la conexión.",
                    ex
            );
        }
    }
}
