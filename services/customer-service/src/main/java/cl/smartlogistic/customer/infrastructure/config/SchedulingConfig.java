package cl.smartlogistic.customer.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Habilita la ejecucion periodica de tareas programadas con {@code @Scheduled},
 * requerida por {@code OutboxDispatcher} para publicar eventos pendientes a
 * RabbitMQ (patron Transactional Outbox, ADR-002).
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {
}
