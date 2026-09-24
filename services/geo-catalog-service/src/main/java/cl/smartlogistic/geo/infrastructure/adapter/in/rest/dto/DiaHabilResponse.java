package cl.smartlogistic.geo.infrastructure.adapter.in.rest.dto;

import java.time.LocalDate;

public record DiaHabilResponse(LocalDate fecha, boolean esDiaHabil) {
}
