package cl.smartlogistic.customer.infrastructure.adapter.out.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

interface ClienteJpaRepository extends JpaRepository<ClienteJpaEntity, UUID> {

    boolean existsByRutNumeroAndRutDv(long rutNumero, String rutDv);

    @Query("""
            select distinct c from ClienteJpaEntity c
            left join c.direcciones d
            where (:comunaCodigo is null or d.comunaCodigo = :comunaCodigo)
            and (:estado is null or c.estado = :estado)
            """)
    Page<ClienteJpaEntity> buscar(@Param("comunaCodigo") String comunaCodigo,
                                   @Param("estado") String estado,
                                   Pageable pageable);
}
