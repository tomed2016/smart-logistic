package cl.smartlogistic.geo.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProvinciaJpaRepository extends JpaRepository<ProvinciaJpaEntity, Integer> {

    List<ProvinciaJpaEntity> findAllByRegionCodigoOrderByCodigoAsc(int regionCodigo);
}
