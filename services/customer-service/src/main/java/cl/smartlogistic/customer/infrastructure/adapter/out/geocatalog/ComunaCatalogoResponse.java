package cl.smartlogistic.customer.infrastructure.adapter.out.geocatalog;

/**
 * Representa el cuerpo JSON devuelto por {@code GET /api/v1/comunas/{codigo}} del
 * servicio {@code geo-catalog-service}. Estructuralmente compatible con
 * {@code cl.smartlogistic.geo.infrastructure.adapter.in.rest.dto.ComunaResponse},
 * pero deliberadamente no compartida como dependencia binaria entre servicios (cada
 * microservicio mantiene su propio contrato de cliente HTTP, tolerante a cambios
 * aditivos en el productor).
 */
public record ComunaCatalogoResponse(int codigo, String nombre, int provinciaCodigo, int regionCodigo) {
}
