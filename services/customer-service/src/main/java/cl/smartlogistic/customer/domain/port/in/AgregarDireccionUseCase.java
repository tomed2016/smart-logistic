package cl.smartlogistic.customer.domain.port.in;

import cl.smartlogistic.customer.domain.model.DireccionId;

public interface AgregarDireccionUseCase {

    DireccionId ejecutar(Comando comando);

    record Comando(
            String clienteId,
            String calle,
            String numero,
            String codigoComuna,
            double latitud,
            double longitud,
            String referencia,
            boolean marcarComoPrincipal,
            String usuario
    ) {
    }
}
