package cl.smartlogistic.qa.support;

import cl.smartlogistic.shared.rut.Rut;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Genera RUTs chilenos validos y (con altisima probabilidad) unicos para usarlos como
 * datos de prueba, sin duplicar el algoritmo oficial del digito verificador: reutiliza
 * el Value Object {@link Rut} del {@code shared-kernel}, que es la unica fuente de
 * verdad del calculo en toda la plataforma.
 *
 * <p>Como {@link Rut#of(String)} no expone el digito verificador esperado para un
 * cuerpo numerico arbitrario (por diseno: solo valida, nunca "genera"), se prueban los
 * 11 valores posibles (0-9 y K) hasta encontrar el unico que {@link Rut#of(String)}
 * acepta. Es intencionalmente mas simple y menos propenso a errores que reimplementar
 * el algoritmo modulo 11 en este modulo.</p>
 */
@Component
public class GeneradorDeRut {

    private static final String DIGITOS_VERIFICADORES_POSIBLES = "0123456789K";

    /** Genera un RUT valido con un cuerpo numerico aleatorio en el rango de personas naturales. */
    public String generarRutValido() {
        long cuerpo = ThreadLocalRandom.current().nextLong(1_000_000L, 25_000_000L);
        for (char digitoVerificador : DIGITOS_VERIFICADORES_POSIBLES.toCharArray()) {
            String candidato = cuerpo + "-" + digitoVerificador;
            try {
                return Rut.of(candidato).formatoCanonico();
            } catch (RuntimeException ignorado) {
                // Digito verificador incorrecto para este cuerpo: se intenta el siguiente.
            }
        }
        throw new IllegalStateException("No fue posible calcular un digito verificador para " + cuerpo);
    }
}
