package cl.smartlogistic.customer.domain.model;

import java.util.Objects;

/**
 * Value Object que representa una comuna chilena dentro de una {@link Region}.
 *
 * <p><b>Nota de alcance (Iteracion 1)</b>: el {@code codigo} es un slug estable
 * generado internamente (ej. {@code PUENTE_ALTO}), <u>no</u> el codigo oficial
 * numerico del INE/SUBDERE. El catalogo inicial cubre las 52 comunas de la Region
 * Metropolitana (zona de operacion inicial del negocio) mas las principales comunas
 * de las otras 15 regiones. El catalogo completo (346 comunas con codigo oficial
 * INE) se importara en la Iteracion 2 al extraer el servicio de Catalogo Geografico
 * compartido; ese cambio sera transparente para este VO porque {@code codigo} seguira
 * siendo estable.</p>
 */
public record Comuna(String codigo, String nombre, Region region) {

    public Comuna {
        Objects.requireNonNull(codigo, "codigo de comuna no puede ser nulo");
        Objects.requireNonNull(nombre, "nombre de comuna no puede ser nulo");
        Objects.requireNonNull(region, "region de comuna no puede ser nula");
        if (codigo.isBlank() || nombre.isBlank()) {
            throw new IllegalArgumentException("codigo y nombre de comuna no pueden estar en blanco");
        }
    }
}
