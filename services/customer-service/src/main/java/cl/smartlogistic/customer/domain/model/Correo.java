package cl.smartlogistic.customer.domain.model;

import java.util.Objects;
import java.util.regex.Pattern;

/** Correo electronico de contacto, con validacion de formato basica (RFC simplificada). */
public record Correo(String direccion) {

    private static final Pattern FORMATO_VALIDO =
            Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");

    public Correo {
        Objects.requireNonNull(direccion, "el correo no puede ser nulo");
        String normalizado = direccion.trim().toLowerCase();
        if (!FORMATO_VALIDO.matcher(normalizado).matches()) {
            throw new IllegalArgumentException("correo con formato invalido: " + direccion);
        }
        direccion = normalizado;
    }
}
