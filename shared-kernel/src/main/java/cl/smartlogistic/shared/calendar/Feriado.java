package cl.smartlogistic.shared.calendar;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Un feriado chileno en una fecha concreta (ya resuelto, sin recurrencia): nombre,
 * fecha calendario y {@link TipoFeriado} de origen para trazabilidad.
 */
public record Feriado(LocalDate fecha, String nombre, TipoFeriado tipo) {

    public Feriado {
        final String FECHA_NULA = "fecha de feriado no puede ser nula";
        final String NOMBRE_NULO = "nombre de feriado no puede ser nulo";
        final String TIPO_NULO = "tipo de feriado no puede ser nulo";
        final String NOMBRE_BLANK = "nombre de feriado no puede estar en blanco";

        Objects.requireNonNull(fecha, FECHA_NULA);
        Objects.requireNonNull(nombre, NOMBRE_NULO);
        Objects.requireNonNull(tipo, TIPO_NULO);
        if (nombre.isBlank()) {
            throw new IllegalArgumentException(NOMBRE_BLANK);
        }
    }
}
