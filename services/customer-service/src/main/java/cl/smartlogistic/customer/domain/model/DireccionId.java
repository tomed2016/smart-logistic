package cl.smartlogistic.customer.domain.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/** Identificador tecnico interno de una {@link Direccion}. */
public final class DireccionId implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final UUID value;

    private DireccionId(UUID value) {
        this.value = value;
    }

    public static DireccionId nuevo() {
        return new DireccionId(UUID.randomUUID());
    }

    public static DireccionId de(UUID value) {
        Objects.requireNonNull(value, "el id de direccion no puede ser nulo");
        return new DireccionId(value);
    }

    public static DireccionId de(String value) {
        Objects.requireNonNull(value, "el id de direccion no puede ser nulo");
        return de(UUID.fromString(value));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DireccionId that)) return false;
        return value.equals(that.value);
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
