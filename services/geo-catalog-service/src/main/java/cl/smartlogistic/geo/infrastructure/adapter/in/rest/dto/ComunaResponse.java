package cl.smartlogistic.geo.infrastructure.adapter.in.rest.dto;

import cl.smartlogistic.geo.domain.model.Comuna;

public record ComunaResponse(int codigo, String nombre, int provinciaCodigo, int regionCodigo) {

    public static ComunaResponse from(Comuna comuna) {
        return new ComunaResponse(comuna.codigo(), comuna.nombre(), comuna.provinciaCodigo(), comuna.regionCodigo());
    }
}
