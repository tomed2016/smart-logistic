package cl.smartlogistic.geo.domain.model;

import java.util.Objects;

/**
 * Una comuna oficial de Chile, perteneciente a una {@link Provincia}. El
 * {@code codigo} corresponde al codigo oficial de comuna del INE/SUBDERE
 * (ej. {@code 13119} para Maipu), fuente autoritativa para todo el catalogo de
 * las 346 comunas vigentes.
 *
 * <p>{@code regionCodigo} se desnormaliza aqui (aunque se puede derivar via
 * {@code provinciaCodigo}) porque las consultas mas frecuentes de este servicio
 * filtran comunas directamente por region (ej. "comunas de la Region
 * Metropolitana"), evitando un join adicional en el caso de uso mas comun.</p>
 */
public record Comuna(int codigo, String nombre, int provinciaCodigo, int regionCodigo) {

    public Comuna {
        Objects.requireNonNull(nombre, "nombre de comuna no puede ser nulo");
        if (nombre.isBlank()) {
            throw new IllegalArgumentException("nombre de comuna no puede estar en blanco");
        }
        if (codigo <= 0) {
            throw new IllegalArgumentException("codigo de comuna debe ser positivo");
        }
        if (provinciaCodigo <= 0) {
            throw new IllegalArgumentException("codigo de provincia debe ser positivo");
        }
        if (regionCodigo <= 0) {
            throw new IllegalArgumentException("codigo de region debe ser positivo");
        }
    }
}
