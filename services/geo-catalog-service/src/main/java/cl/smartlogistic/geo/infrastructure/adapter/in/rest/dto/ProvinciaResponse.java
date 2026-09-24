package cl.smartlogistic.geo.infrastructure.adapter.in.rest.dto;

import cl.smartlogistic.geo.domain.model.Provincia;

public record ProvinciaResponse(int codigo, String nombre, int regionCodigo) {

    public static ProvinciaResponse from(Provincia provincia) {
        return new ProvinciaResponse(provincia.codigo(), provincia.nombre(), provincia.regionCodigo());
    }
}
