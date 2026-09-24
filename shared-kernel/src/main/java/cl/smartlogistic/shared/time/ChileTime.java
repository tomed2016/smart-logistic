package cl.smartlogistic.shared.time;

import cl.smartlogistic.shared.calendar.CalendarioChileno;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Punto unico de acceso a la zona horaria de negocio del sistema:
 * {@code America/Santiago}. Todos los servicios deben usar esta clase en lugar de
 * {@code ZoneId.systemDefault()} para evitar inconsistencias cuando los contenedores
 * corren en una nube con otra zona horaria por defecto (ej. UTC).
 *
 * <p>Desde la Iteracion 2, el calculo de dia habil delega en
 * {@link CalendarioChileno}, que considera feriados legales chilenos ademas de
 * fines de semana. Ver {@code docs/architecture/04-bounded-context-catalogo-geografico.md}.</p>
 */
public final class ChileTime {

    public static final ZoneId ZONA_HORARIA = ZoneId.of("America/Santiago");

    private static final CalendarioChileno CALENDARIO = new CalendarioChileno();

    private ChileTime() {
    }

    public static ZonedDateTime ahora() {
        return ZonedDateTime.now(ZONA_HORARIA);
    }

    public static LocalDate hoy() {
        return LocalDate.now(ZONA_HORARIA);
    }

    public static boolean esFinDeSemana(LocalDate fecha) {
        DayOfWeek dia = fecha.getDayOfWeek();
        return dia == DayOfWeek.SATURDAY || dia == DayOfWeek.SUNDAY;
    }

    /**
     * @return true si {@code fecha} es feriado legal chileno (ver {@link CalendarioChileno}).
     */
    public static boolean esFeriado(LocalDate fecha) {
        return CALENDARIO.esFeriado(fecha);
    }

    /**
     * @return true si {@code fecha} es dia habil: no es fin de semana ni feriado.
     */
    public static boolean esDiaHabil(LocalDate fecha) {
        return CALENDARIO.esDiaHabil(fecha);
    }

    /**
     * Siguiente dia habil (a partir de {@code desde}, sin incluirlo), considerando
     * fines de semana y feriados legales chilenos.
     */
    public static LocalDate siguienteDiaHabil(LocalDate desde) {
        return CALENDARIO.siguienteDiaHabil(desde);
    }

    /**
     * @deprecated usar {@link #siguienteDiaHabil(LocalDate)}, que ademas de fines de
     * semana considera feriados legales chilenos via {@link CalendarioChileno}.
     */
    @Deprecated(forRemoval = true)
    public static LocalDate siguienteDiaHabilAproximado(LocalDate desde) {
        LocalDate candidato = desde.plusDays(1);
        while (esFinDeSemana(candidato)) {
            candidato = candidato.plusDays(1);
        }
        return candidato;
    }
}
