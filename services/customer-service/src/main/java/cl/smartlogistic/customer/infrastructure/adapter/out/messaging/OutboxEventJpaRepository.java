package cl.smartlogistic.customer.infrastructure.adapter.out.messaging;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface OutboxEventJpaRepository extends JpaRepository<OutboxEventJpaEntity, UUID> {

    List<OutboxEventJpaEntity> findByPublishedAtIsNullOrderByOccurredOnAsc(Pageable pageable);
}
