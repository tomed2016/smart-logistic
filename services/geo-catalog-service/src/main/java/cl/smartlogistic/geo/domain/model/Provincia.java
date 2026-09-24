package cl.smartlogistic.geo.domain.model;

import java.util.Objects;

/**
 * Una provincia oficial de Chile, perteneciente a una {@link Region}. El
 * {@code codigo} corresponde al codigo oficial de provincia del INE/SUBDERE
 * (ej. {@code 131} para la provincia de Santiago).
 */
public record Provincia(int codigo, String nombre, int regionCodigo) {

    public Provincia {
        Objects.requireNonNull(nombre, "nombre de provincia no puede ser nulo");
        if (nombre.isBlank()) {
            throw new IllegalArgumentException("nombre de provincia no puede estar en blanco");
        }
        if (codigo <= 0) {
            throw new IllegalArgumentException("codigo de provincia debe ser positivo");
        }
        if (regionCodigo <= 0) {
            throw new IllegalArgumentException("codigo de region debe ser positivo");
        }
    }
}
