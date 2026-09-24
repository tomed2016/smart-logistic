package cl.smartlogistic.geo.infrastructure.adapter.out.persistence;

import cl.smartlogistic.geo.domain.model.Provincia;
import cl.smartlogistic.geo.domain.port.out.ProvinciaRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ProvinciaRepositoryAdapter implements ProvinciaRepositoryPort {

    private final ProvinciaJpaRepository jpaRepository;

    public ProvinciaRepositoryAdapter(ProvinciaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<Provincia> listarPorRegion(int regionCodigo) {
        return jpaRepository.findAllByRegionCodigoOrderByCodigoAsc(regionCodigo).stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Provincia> buscarPorCodigo(int codigo) {
        return jpaRepository.findById(codigo).map(this::toDomain);
    }

    private Provincia toDomain(ProvinciaJpaEntity entity) {
        return new Provincia(entity.getCodigo(), entity.getNombre(), entity.getRegionCodigo());
    }
}
