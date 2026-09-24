package cl.smartlogistic.customer.infrastructure.adapter.in.rest.dto;

public record DireccionResponse(
        String id,
        String calle,
        String numero,
        String comunaCodigo,
        String comunaNombre,
        String region,
        double latitud,
        double longitud,
        String referencia,
        boolean esPrincipal
) {
}
