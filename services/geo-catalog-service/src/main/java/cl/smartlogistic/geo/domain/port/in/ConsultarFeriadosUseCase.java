package cl.smartlogistic.geo.domain.port.in;

import cl.smartlogistic.shared.calendar.Feriado;

import java.time.LocalDate;
import java.util.List;

/**
 * Caso de uso de consulta de feriados legales chilenos para un anio dado.
 * Delega el calculo en {@code CalendarioChileno} (shared-kernel): este servicio
 * no persiste feriados, los expone como una vista de solo lectura sobre la logica
 * de dominio compartida, evitando duplicar/desincronizar la regla de negocio entre
 * la libreria y el servicio.
 */
public interface ConsultarFeriadosUseCase {

    List<Feriado> feriadosDelAnio(int anio);

    boolean esDiaHabil(LocalDate fecha);

    LocalDate siguienteDiaHabil(LocalDate desde);
}
