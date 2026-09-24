package cl.smartlogistic.customer.domain.port.in;

import cl.smartlogistic.customer.domain.model.Cliente;
import cl.smartlogistic.customer.domain.port.out.ClienteRepository;

public interface ConsultarClienteUseCase {

    Cliente obtenerPorId(String clienteId);

    ClienteRepository.PaginaClientes listar(String codigoComuna, String estado, int pagina, int tamanoPagina);
}
