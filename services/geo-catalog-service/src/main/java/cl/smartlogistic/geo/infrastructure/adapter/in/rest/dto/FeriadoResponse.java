package cl.smartlogistic.geo.infrastructure.adapter.in.rest.dto;

import cl.smartlogistic.shared.calendar.Feriado;
import cl.smartlogistic.shared.calendar.TipoFeriado;

import java.time.LocalDate;

public record FeriadoResponse(LocalDate fecha, String nombre, TipoFeriado tipo) {

    public static FeriadoResponse from(Feriado feriado) {
        return new FeriadoResponse(feriado.fecha(), feriado.nombre(), feriado.tipo());
    }
}
