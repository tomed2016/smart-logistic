package cl.smartlogistic.geo.infrastructure.adapter.in.rest.dto;

import cl.smartlogistic.geo.domain.model.Region;

public record RegionResponse(int codigo, String nombre) {

    public static RegionResponse from(Region region) {
        return new RegionResponse(region.codigo(), region.nombre());
    }
}
