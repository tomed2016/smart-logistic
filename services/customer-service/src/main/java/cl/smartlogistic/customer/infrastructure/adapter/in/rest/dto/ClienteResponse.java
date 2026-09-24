package cl.smartlogistic.customer.infrastructure.adapter.in.rest.dto;

import cl.smartlogistic.customer.domain.model.Cliente;
import cl.smartlogistic.customer.domain.model.Direccion;

import java.time.Instant;
import java.util.List;

public record ClienteResponse(
        String id,
        String rut,
        String tipoCliente,
        String nombre,
        List<String> telefonos,
        List<String> correos,
        String condicionPago,
        String prioridadComercial,
        String estado,
        List<DireccionResponse> direcciones,
        Instant creadoEn,
        Instant actualizadoEn
) {

    public static ClienteResponse desde(Cliente cliente) {
        return new ClienteResponse(
                cliente.id().value().toString(),
                cliente.rut().formatoPresentacion(),
                cliente.tipoCliente().name(),
                cliente.nombre(),
                cliente.telefonos().stream().map(t -> t.numero()).toList(),
                cliente.correos().stream().map(c -> c.direccion()).toList(),
                cliente.condicionPago().name(),
                cliente.prioridadComercial().name(),
                cliente.estado().name(),
                cliente.direcciones().stream().map(ClienteResponse::desdeDireccion).toList(),
                cliente.auditInfo().creadoEn(),
                cliente.auditInfo().actualizadoEn());
    }

    private static DireccionResponse desdeDireccion(Direccion direccion) {
        return new DireccionResponse(
                direccion.id().value().toString(),
                direccion.calle(),
                direccion.numero(),
                direccion.comuna().codigo(),
                direccion.comuna().nombre(),
                direccion.comuna().region().name(),
                direccion.coordenadas().latitud(),
                direccion.coordenadas().longitud(),
                direccion.referencia(),
                direccion.esPrincipal());
    }
}
