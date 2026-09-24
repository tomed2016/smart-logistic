package cl.smartlogistic.geo.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ComunaJpaRepository extends JpaRepository<ComunaJpaEntity, Integer> {

    List<ComunaJpaEntity> findAllByProvinciaCodigoOrderByCodigoAsc(int provinciaCodigo);

    List<ComunaJpaEntity> findAllByOrderByCodigoAsc();

    @Query("""
            select c from ComunaJpaEntity c
            where c.provinciaCodigo in (
                select p.codigo from ProvinciaJpaEntity p where p.regionCodigo = :regionCodigo
            )
            order by c.codigo asc
            """)
    List<ComunaJpaEntity> findAllByRegionCodigo(@Param("regionCodigo") int regionCodigo);

    @Query("""
            select c from ComunaJpaEntity c
            where lower(c.nombre) like lower(concat('%', :nombreParcial, '%'))
            order by c.codigo asc
            """)
    List<ComunaJpaEntity> findAllByNombreContainingIgnoreCase(@Param("nombreParcial") String nombreParcial);
}
