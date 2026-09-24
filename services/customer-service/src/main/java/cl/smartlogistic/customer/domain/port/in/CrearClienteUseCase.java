package cl.smartlogistic.customer.domain.port.in;

import cl.smartlogistic.customer.domain.model.ClienteId;
import cl.smartlogistic.customer.domain.model.CondicionPago;
import cl.smartlogistic.customer.domain.model.PrioridadComercial;
import cl.smartlogistic.customer.domain.model.TipoCliente;

import java.util.List;

public interface CrearClienteUseCase {

    ClienteId ejecutar(Comando comando);

    record Comando(
            String rut,
            TipoCliente tipoCliente,
            String nombre,
            List<String> telefonos,
            List<String> correos,
            CondicionPago condicionPago,
            PrioridadComercial prioridadComercial,
            String usuario
    ) {
    }
}
