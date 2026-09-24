package cl.smartlogistic.customer.domain.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Identificador tecnico interno de un {@link Cliente}. Es el unico identificador
 * usado como clave primaria/foranea entre servicios; el {@code Rut} del cliente
 * jamas cumple ese rol (ver ADR-001 / bounded context Clientes).
 */
public final class ClienteId implements Serializable {

    private final UUID value;

    private ClienteId(UUID value) {
        this.value = value;
    }

    public static ClienteId nuevo() {
        return new ClienteId(UUID.randomUUID());
    }

    public static ClienteId de(UUID value) {
        Objects.requireNonNull(value, "el id de cliente no puede ser nulo");
        return new ClienteId(value);
    }

    public static ClienteId de(String value) {
        Objects.requireNonNull(value, "el id de cliente no puede ser nulo");
        return de(UUID.fromString(value));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClienteId clienteId)) return false;
        return value.equals(clienteId.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
