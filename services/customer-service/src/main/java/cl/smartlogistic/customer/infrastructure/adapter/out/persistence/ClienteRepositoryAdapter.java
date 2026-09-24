package cl.smartlogistic.customer.infrastructure.adapter.out.persistence;

import cl.smartlogistic.customer.domain.model.Cliente;
import cl.smartlogistic.customer.domain.model.ClienteId;
import cl.smartlogistic.customer.domain.port.out.ClienteRepository;
import cl.smartlogistic.shared.rut.Rut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
class ClienteRepositoryAdapter implements ClienteRepository {

    private final ClienteJpaRepository clienteJpaRepository;

    ClienteRepositoryAdapter(ClienteJpaRepository clienteJpaRepository) {
        this.clienteJpaRepository = clienteJpaRepository;
    }

    @Override
    public Cliente guardar(Cliente cliente) {
        ClienteJpaEntity entidad = ClienteMapper.aEntidad(cliente);
        ClienteJpaEntity persistida = clienteJpaRepository.save(entidad);
        return ClienteMapper.aDominio(persistida);
    }

    @Override
    public Optional<Cliente> buscarPorId(ClienteId id) {
        return clienteJpaRepository.findById(id.value()).map(ClienteMapper::aDominio);
    }

    @Override
    public boolean existePorRut(Rut rut) {
        return clienteJpaRepository.existsByRutNumeroAndRutDv(rut.numero(), String.valueOf(rut.digitoVerificador()));
    }

    @Override
    public PaginaClientes buscar(FiltroClientes filtro, int pagina, int tamanoPagina) {
        Page<ClienteJpaEntity> resultado = clienteJpaRepository.buscar(
                filtro.codigoComuna(), filtro.estado(), PageRequest.of(pagina, tamanoPagina));
        List<Cliente> contenido = resultado.getContent().stream().map(ClienteMapper::aDominio).toList();
        return new PaginaClientes(contenido, resultado.getTotalElements(), pagina, tamanoPagina);
    }
}
