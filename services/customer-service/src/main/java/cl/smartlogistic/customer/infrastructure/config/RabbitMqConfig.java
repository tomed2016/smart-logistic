package cl.smartlogistic.customer.infrastructure.config;

import cl.smartlogistic.customer.infrastructure.adapter.out.messaging.RabbitMqProperties;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Declara el exchange topic {@code clientes.events} usado para publicar los
 * eventos de dominio del bounded context Clientes (ver ADR-002). Los servicios
 * consumidores (Pedidos, Planificacion Logistica) declaran sus propias colas y las
 * enlazan a este exchange con el routing key correspondiente (ej.
 * {@code cliente.creado.v1}).
 */
@Configuration
@EnableConfigurationProperties(RabbitMqProperties.class)
public class RabbitMqConfig {

    @Bean
    public TopicExchange clientesEventsExchange(RabbitMqProperties properties) {
        return new TopicExchange(properties.exchangeName(), true, false);
    }
}
