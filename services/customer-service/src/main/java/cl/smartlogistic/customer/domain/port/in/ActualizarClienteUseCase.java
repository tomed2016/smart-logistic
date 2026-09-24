package cl.smartlogistic.customer.domain.port.in;

import cl.smartlogistic.customer.domain.model.CondicionPago;
import cl.smartlogistic.customer.domain.model.PrioridadComercial;

import java.util.List;

public interface ActualizarClienteUseCase {

    void ejecutar(Comando comando);

    record Comando(
            String clienteId,
            String nombre,
            List<String> telefonos,
            List<String> correos,
            CondicionPago condicionPago,
            PrioridadComercial prioridadComercial,
            String usuario
    ) {
    }
}
