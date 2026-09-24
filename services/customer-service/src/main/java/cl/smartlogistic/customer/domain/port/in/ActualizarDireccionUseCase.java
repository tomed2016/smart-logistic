package cl.smartlogistic.customer.domain.port.in;

public interface ActualizarDireccionUseCase {

    void ejecutar(Comando comando);

    record Comando(
            String clienteId,
            String direccionId,
            String calle,
            String numero,
            String codigoComuna,
            double latitud,
            double longitud,
            String referencia,
            String usuario
    ) {
    }
}
