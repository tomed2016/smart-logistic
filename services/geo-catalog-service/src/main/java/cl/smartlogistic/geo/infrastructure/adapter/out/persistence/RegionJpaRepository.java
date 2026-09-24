package cl.smartlogistic.geo.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegionJpaRepository extends JpaRepository<RegionJpaEntity, Integer> {

    List<RegionJpaEntity> findAllByOrderByCodigoAsc();
}
