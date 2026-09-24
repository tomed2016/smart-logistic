package cl.smartlogistic.customer.domain.model;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Numero de telefono de contacto. Acepta formato chileno tipico
 * ({@code +56 9 1234 5678}, {@code +5691234 5678}, {@code 912345678}) sin ser
 * excesivamente estricto, ya que el formato exacto de digitacion varia por canal
 * de ingreso (web, telefonico).
 */
public record Telefono(String numero) {

    private static final Pattern FORMATO_VALIDO = Pattern.compile("^\\+?\\d{8,15}$");

    public Telefono {
        Objects.requireNonNull(numero, "el telefono no puede ser nulo");
        String normalizado = numero.replaceAll("[\\s\\-()]", "");
        if (!FORMATO_VALIDO.matcher(normalizado).matches()) {
            throw new IllegalArgumentException("telefono con formato invalido: " + numero);
        }
        numero = normalizado;
    }
}
