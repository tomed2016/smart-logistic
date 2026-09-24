package cl.smartlogistic.shared.time;

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
 * <p>Nota: no considera aun el calendario de feriados chilenos (se incorpora en la
 * Iteracion 2, servicio de Catalogo Geografico / Calendario Habil), solo fines de
 * semana.</p>
 */
public final class ChileTime {

    public static final ZoneId ZONA_HORARIA = ZoneId.of("America/Santiago");

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
     * Siguiente dia (a partir de {@code desde}, sin incluirlo) que no cae en fin de
     * semana. No considera feriados (ver nota de clase); se reemplazara en la
     * Iteracion 2 por una version que consulte el calendario de feriados chilenos.
     */
    public static LocalDate siguienteDiaHabilAproximado(LocalDate desde) {
        LocalDate candidato = desde.plusDays(1);
        while (esFinDeSemana(candidato)) {
            candidato = candidato.plusDays(1);
        }
        return candidato;
    }
}
