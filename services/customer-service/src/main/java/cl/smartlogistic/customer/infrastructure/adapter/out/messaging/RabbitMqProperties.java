package cl.smartlogistic.customer.infrastructure.adapter.out.messaging;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "smart-logistic.rabbitmq")
public record RabbitMqProperties(String exchangeName) {

    public RabbitMqProperties {
        if (exchangeName == null || exchangeName.isBlank()) {
            exchangeName = "clientes.events";
        }
    }
}
