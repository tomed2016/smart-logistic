package cl.smartlogistic.geo.infrastructure.adapter.out.persistence;

import cl.smartlogistic.geo.domain.model.Region;
import cl.smartlogistic.geo.domain.port.out.RegionRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class RegionRepositoryAdapter implements RegionRepositoryPort {

    private final RegionJpaRepository jpaRepository;

    public RegionRepositoryAdapter(RegionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<Region> listarTodas() {
        return jpaRepository.findAllByOrderByCodigoAsc().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Region> buscarPorCodigo(int codigo) {
        return jpaRepository.findById(codigo).map(this::toDomain);
    }

    private Region toDomain(RegionJpaEntity entity) {
        return new Region(entity.getCodigo(), entity.getNombre());
    }
}
