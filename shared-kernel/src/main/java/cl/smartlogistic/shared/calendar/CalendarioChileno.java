package cl.smartlogistic.shared.calendar;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Calendario de feriados chilenos y utilidades de dia habil, con logica pura de
 * dominio (sin dependencias de framework), para ser usado tanto por {@code
 * shared-kernel} como por cualquier servicio (ej. planificacion logistica,
 * generacion de ocurrencias recurrentes).
 *
 * <p><b>Estrategia de calculo (transparencia deliberada, ver ADR asociado en
 * {@code docs/architecture/04-bounded-context-catalogo-geografico.md})</b>:</p>
 * <ul>
 *   <li><b>Feriados fijos</b>: fecha calendario estable, definidos por leyes
 *   permanentes vigentes (Año Nuevo, Día del Trabajo, Glorias Navales, Virgen del
 *   Carmen, Asunción de la Virgen, Independencia Nacional, Glorias del Ejército,
 *   Iglesias Evangélicas y Protestantes, Todos los Santos, Inmaculada Concepción,
 *   Navidad).</li>
 *   <li><b>Feriados moviles por Pascua</b>: Viernes Santo y Sabado Santo, calculados
 *   mediante el algoritmo de Meeus/Jones/Butcher (calendario gregoriano),
 *   matematicamente deterministico y verificable para cualquier anio.</li>
 *   <li><b>Feriados moviles por Ley 19.668</b>: San Pedro y San Pablo (29 de junio) y
 *   Encuentro de Dos Mundos (12 de octubre) se trasladan al lunes mas cercano segun
 *   la regla legal: si la fecha cae martes/miercoles/jueves se traslada al lunes
 *   anterior; si cae viernes, se traslada al lunes siguiente. Si cae lunes, sabado o
 *   domingo, no hay traslado.</li>
 *   <li><b>Feriados ad-hoc</b>: leyes puntuales que declaran un feriado adicional
 *   para un anio especifico (ej. dias "puente" de Fiestas Patrias) <b>no</b> se
 *   infieren ni se fabrican por adivinanza: deben incorporarse explicitamente via el
 *   constructor {@link #CalendarioChileno(Set)} cuando la ley correspondiente exista
 *   y haya sido verificada contra una fuente oficial (Diario Oficial / BCN). Por
 *   defecto (constructor sin argumentos) esta lista esta vacia.</li>
 * </ul>
 */
public final class CalendarioChileno {

    private final Set<LocalDate> feriadosAdHoc;

    /** Calendario sin feriados ad-hoc adicionales (solo los legales permanentes). */
    public CalendarioChileno() {
        this(Set.of());
    }

    /**
     * @param feriadosAdHoc fechas adicionales, verificadas contra una fuente oficial,
     *                       declaradas por leyes puntuales para anios especificos.
     */
    public CalendarioChileno(Set<LocalDate> feriadosAdHoc) {
        this.feriadosAdHoc = feriadosAdHoc == null ? Set.of() : Set.copyOf(feriadosAdHoc);
    }

    /**
     * Todos los feriados del anio dado, ordenados por fecha.
     */
    public List<Feriado> feriadosDelAnio(int anio) {
        Set<Feriado> feriados = new TreeSet<>((a, b) -> a.fecha().compareTo(b.fecha()));

        feriados.add(new Feriado(LocalDate.of(anio, Month.JANUARY, 1), "Año Nuevo", TipoFeriado.FIJO));

        LocalDate domingoPascua = domingoDePascua(anio);
        feriados.add(new Feriado(domingoPascua.minusDays(2), "Viernes Santo", TipoFeriado.MOVIL_PASCUA));
        feriados.add(new Feriado(domingoPascua.minusDays(1), "Sábado Santo", TipoFeriado.MOVIL_PASCUA));

        feriados.add(new Feriado(LocalDate.of(anio, Month.MAY, 1), "Día Nacional del Trabajo", TipoFeriado.FIJO));
        feriados.add(new Feriado(LocalDate.of(anio, Month.MAY, 21), "Día de las Glorias Navales", TipoFeriado.FIJO));

        feriados.add(trasladoLey19668(LocalDate.of(anio, Month.JUNE, 29), "San Pedro y San Pablo"));

        feriados.add(new Feriado(LocalDate.of(anio, Month.JULY, 16), "Día de la Virgen del Carmen", TipoFeriado.FIJO));
        feriados.add(new Feriado(LocalDate.of(anio, Month.AUGUST, 15), "Asunción de la Virgen", TipoFeriado.FIJO));
        feriados.add(new Feriado(LocalDate.of(anio, Month.SEPTEMBER, 18), "Independencia Nacional", TipoFeriado.FIJO));
        feriados.add(new Feriado(LocalDate.of(anio, Month.SEPTEMBER, 19), "Día de las Glorias del Ejército", TipoFeriado.FIJO));

        feriados.add(trasladoLey19668(LocalDate.of(anio, Month.OCTOBER, 12), "Encuentro de Dos Mundos"));

        feriados.add(new Feriado(LocalDate.of(anio, Month.OCTOBER, 31), "Día de las Iglesias Evangélicas y Protestantes", TipoFeriado.FIJO));
        feriados.add(new Feriado(LocalDate.of(anio, Month.NOVEMBER, 1), "Día de Todos los Santos", TipoFeriado.FIJO));
        feriados.add(new Feriado(LocalDate.of(anio, Month.DECEMBER, 8), "Inmaculada Concepción", TipoFeriado.FIJO));
        feriados.add(new Feriado(LocalDate.of(anio, Month.DECEMBER, 25), "Navidad", TipoFeriado.FIJO));

        for (LocalDate fecha : feriadosAdHoc) {
            if (fecha.getYear() == anio) {
                feriados.add(new Feriado(fecha, "Feriado ad-hoc (ley puntual)", TipoFeriado.AD_HOC));
            }
        }

        return new ArrayList<>(feriados);
    }

    /**
     * @return true si {@code fecha} es feriado segun este calendario.
     */
    public boolean esFeriado(LocalDate fecha) {
        return feriadosDelAnio(fecha.getYear()).stream().anyMatch(f -> f.fecha().equals(fecha));
    }

    public boolean esFinDeSemana(LocalDate fecha) {
        DayOfWeek dia = fecha.getDayOfWeek();
        return dia == DayOfWeek.SATURDAY || dia == DayOfWeek.SUNDAY;
    }

    /**
     * @return true si {@code fecha} es dia habil: no es fin de semana ni feriado.
     */
    public boolean esDiaHabil(LocalDate fecha) {
        return !esFinDeSemana(fecha) && !esFeriado(fecha);
    }

    /**
     * Siguiente dia habil estrictamente posterior a {@code desde} (no incluye
     * {@code desde} aunque este sea habil). Util para planificar la entrega del
     * "siguiente dia habil".
     */
    public LocalDate siguienteDiaHabil(LocalDate desde) {
        LocalDate candidato = desde.plusDays(1);
        while (!esDiaHabil(candidato)) {
            candidato = candidato.plusDays(1);
        }
        return candidato;
    }

    /**
     * @return {@code fecha} si ya es dia habil, o el siguiente dia habil en caso
     *         contrario (a diferencia de {@link #siguienteDiaHabil(LocalDate)}, que
     *         siempre avanza al menos un dia).
     */
    public LocalDate diaHabilMasCercano(LocalDate fecha) {
        return esDiaHabil(fecha) ? fecha : siguienteDiaHabil(fecha.minusDays(1));
    }

    /**
     * Domingo de Pascua para el anio dado, calculado mediante el algoritmo de
     * Meeus/Jones/Butcher (calendario gregoriano).
     */
    static LocalDate domingoDePascua(int anio) {
        int a = anio % 19;
        int b = anio / 100;
        int c = anio % 100;
        int d = b / 4;
        int e = b % 4;
        int f = (b + 8) / 25;
        int g = (b - f + 1) / 3;
        int h = (19 * a + b - d - g + 15) % 30;
        int i = c / 4;
        int k = c % 4;
        int l = (32 + 2 * e + 2 * i - h - k) % 7;
        int m = (a + 11 * h + 22 * l) / 451;
        int mes = (h + l - 7 * m + 114) / 31;
        int dia = ((h + l - 7 * m + 114) % 31) + 1;
        return LocalDate.of(anio, mes, dia);
    }

    /**
     * Aplica la regla de traslado de la Ley 19.668: martes/miercoles/jueves -> lunes
     * anterior; viernes -> lunes siguiente; lunes/sabado/domingo -> sin traslado.
     */
    private static Feriado trasladoLey19668(LocalDate fechaBase, String nombre) {
        DayOfWeek dia = fechaBase.getDayOfWeek();
        LocalDate fechaFinal = switch (dia) {
            case TUESDAY, WEDNESDAY, THURSDAY -> fechaBase.with(java.time.temporal.TemporalAdjusters.previous(DayOfWeek.MONDAY));
            case FRIDAY -> fechaBase.with(java.time.temporal.TemporalAdjusters.next(DayOfWeek.MONDAY));
            default -> fechaBase;
        };
        return new Feriado(fechaFinal, nombre, dia == DayOfWeek.MONDAY ? TipoFeriado.FIJO : TipoFeriado.MOVIL_LEY_19668);
    }
}
