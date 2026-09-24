package cl.smartlogistic.customer.domain.port.in;

public interface DesactivarClienteUseCase {

    void ejecutar(Comando comando);

    record Comando(String clienteId, String usuario) {
    }
}
