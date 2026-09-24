package cl.smartlogistic.shared.calendar;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Un feriado chileno en una fecha concreta (ya resuelto, sin recurrencia): nombre,
 * fecha calendario y {@link TipoFeriado} de origen para trazabilidad.
 */
public record Feriado(LocalDate fecha, String nombre, TipoFeriado tipo) {

    public Feriado {
        Objects.requireNonNull(fecha, "fecha de feriado no puede ser nula");
        Objects.requireNonNull(nombre, "nombre de feriado no puede ser nulo");
        Objects.requireNonNull(tipo, "tipo de feriado no puede ser nulo");
        if (nombre.isBlank()) {
            throw new IllegalArgumentException("nombre de feriado no puede estar en blanco");
        }
    }
}
