package org.ide.dbp_proyecto.service;
import static java.lang.Math.*;

import org.springframework.stereotype.Service;

@Service
public class GeolocalizacionService {
    public static final double RADIO_CHECKIN_METROS = 50.0;
    private static final double RADIO_TIERRA_METROS = 6_371_000.0;

    public double calcularDistancia(double latUsuario, double lonUsuario, double latPoi, double lonPoi) {
        double phi1    = toRadians(latUsuario);
        double phi2    = toRadians(latPoi);
        double lambda1 = toRadians(lonUsuario);
        double lambda2 = toRadians(lonPoi);

        // Formula de Haversine para hallar la distancia entre dos puntos
        double distancia = 2 * RADIO_TIERRA_METROS * asin(sqrt(
                pow(sin((phi2 - phi1) / 2), 2) +
                        cos(phi1) * cos(phi2) * pow(sin((lambda2 - lambda1) / 2), 2)
        ));

        return distancia;
    }
}
