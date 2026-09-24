package cl.smartlogistic.customer.infrastructure.adapter.in.rest.dto;

import cl.smartlogistic.customer.domain.port.out.ClienteRepository;

import java.util.List;

public record PaginaClientesResponse(
        List<ClienteResponse> contenido,
        long totalElementos,
        int pagina,
        int tamanoPagina
) {
    public static PaginaClientesResponse desde(ClienteRepository.PaginaClientes pagina) {
        return new PaginaClientesResponse(
                pagina.contenido().stream().map(ClienteResponse::desde).toList(),
                pagina.totalElementos(),
                pagina.pagina(),
                pagina.tamanoPagina());
    }
}
