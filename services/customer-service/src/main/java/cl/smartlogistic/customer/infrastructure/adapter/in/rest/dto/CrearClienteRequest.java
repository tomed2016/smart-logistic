package cl.smartlogistic.customer.infrastructure.adapter.in.rest.dto;

import cl.smartlogistic.customer.domain.model.CondicionPago;
import cl.smartlogistic.customer.domain.model.PrioridadComercial;
import cl.smartlogistic.customer.domain.model.TipoCliente;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CrearClienteRequest(
        @NotBlank String rut,
        @NotNull TipoCliente tipoCliente,
        @NotBlank String nombre,
        List<String> telefonos,
        List<String> correos,
        @NotNull CondicionPago condicionPago,
        @NotNull PrioridadComercial prioridadComercial
) {
    public CrearClienteRequest {
        telefonos = telefonos == null ? List.of() : telefonos;
        correos = correos == null ? List.of() : correos;
    }
}
