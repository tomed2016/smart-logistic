package cl.smartlogistic.geo.domain.model;

import java.util.Objects;

/**
 * Una region oficial de Chile (division politico-administrativa de primer nivel).
 * El {@code codigo} corresponde al codigo oficial de region del INE/SUBDERE
 * (ej. {@code 13} para la Region Metropolitana de Santiago), a diferencia del
 * enum interno {@code Region} usado en {@code customer-service}, que es un
 * catalogo cerrado sin dependencia de este servicio. Ver ADR de coexistencia en
 * {@code docs/architecture/04-bounded-context-catalogo-geografico.md}.
 */
public record Region(int codigo, String nombre) {

    public Region {
        Objects.requireNonNull(nombre, "nombre de region no puede ser nulo");
        if (nombre.isBlank()) {
            throw new IllegalArgumentException("nombre de region no puede estar en blanco");
        }
        if (codigo <= 0) {
            throw new IllegalArgumentException("codigo de region debe ser positivo");
        }
    }
}
