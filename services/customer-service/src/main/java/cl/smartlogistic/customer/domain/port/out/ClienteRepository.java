package cl.smartlogistic.customer.domain.port.out;

import cl.smartlogistic.customer.domain.model.Cliente;
import cl.smartlogistic.customer.domain.model.ClienteId;
import cl.smartlogistic.shared.rut.Rut;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida (repositorio) para el agregado {@link Cliente}. Su implementacion
 * concreta (adaptador JPA) vive en la capa de infraestructura.
 */
public interface ClienteRepository {

    Cliente guardar(Cliente cliente);

    Optional<Cliente> buscarPorId(ClienteId id);

    boolean existePorRut(Rut rut);

    /** Listado paginado con filtros opcionales de comuna y estado. */
    PaginaClientes buscar(FiltroClientes filtro, int pagina, int tamanoPagina);

    record FiltroClientes(String codigoComuna, String estado) {
    }

    record PaginaClientes(List<Cliente> contenido, long totalElementos, int pagina, int tamanoPagina) {
    }
}
