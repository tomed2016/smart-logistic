package cl.smartlogistic.customer.domain.model;

/**
 * Coordenadas geograficas (WGS84) de una direccion, usadas por Planificacion
 * Logistica para el calculo de rutas.
 */
public record Coordenadas(double latitud, double longitud) {

    public Coordenadas {
        if (latitud < -90.0 || latitud > 90.0) {
            throw new IllegalArgumentException("latitud fuera de rango [-90, 90]: " + latitud);
        }
        if (longitud < -180.0 || longitud > 180.0) {
            throw new IllegalArgumentException("longitud fuera de rango [-180, 180]: " + longitud);
        }
    }
}
