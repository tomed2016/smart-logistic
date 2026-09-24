package cl.smartlogistic.shared.rut;

import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object que representa un RUT (Rol Unico Tributario) chileno, ya validado
 * segun el algoritmo oficial de digito verificador (modulo 11).
 *
 * <p><b>Regla de negocio critica</b>: el RUT identifica legalmente a una persona o
 * empresa, pero <u>nunca</u> debe usarse como identificador tecnico interno (clave
 * primaria/foranea) de ningun agregado. Cada agregado usa su propio {@code UUID}
 * (ej. {@code ClienteId}); el RUT se persiste como atributo con restriccion de
 * unicidad de negocio.</p>
 *
 * <p>Instancias inmutables, construidas exclusivamente a traves de {@link #of(String)},
 * lo que garantiza que nunca exista en el sistema un {@code Rut} invalido.</p>
 */
public final class Rut implements Serializable, Comparable<Rut> {

    private static final Pattern CARACTERES_A_LIMPIAR = Pattern.compile("[.\\-\\s]");
    private static final Pattern CUERPO_NUMERICO = Pattern.compile("^\\d{7,8}$");

    private final long numero;
    private final char digitoVerificador;

    private Rut(long numero, char digitoVerificador) {
        this.numero = numero;
        this.digitoVerificador = digitoVerificador;
    }

    /**
     * Crea y valida un {@link Rut} a partir de un texto en cualquier formato usual
     * chileno: {@code 12.345.678-5}, {@code 12345678-5} o {@code 123456785}.
     *
     * @throws InvalidRutException si el formato es incorrecto o el digito
     *                              verificador no corresponde.
     */
    public static Rut of(String rutTexto) {
        Objects.requireNonNull(rutTexto, "El RUT no puede ser nulo");
        String limpio = CARACTERES_A_LIMPIAR.matcher(rutTexto.trim().toUpperCase()).replaceAll("");
        if (limpio.length() < 8 || limpio.length() > 9) {
            throw new InvalidRutException(rutTexto,
                    "se esperan 7 u 8 digitos en el cuerpo mas 1 digito verificador");
        }
        String cuerpo = limpio.substring(0, limpio.length() - 1);
        char digitoVerificadorIngresado = limpio.charAt(limpio.length() - 1);
        if (!CUERPO_NUMERICO.matcher(cuerpo).matches()) {
            throw new InvalidRutException(rutTexto, "el cuerpo del RUT debe contener solo digitos");
        }

        long numero = Long.parseLong(cuerpo);
        char digitoVerificadorEsperado = calcularDigitoVerificador(numero);
        if (digitoVerificadorIngresado != digitoVerificadorEsperado) {
            throw new InvalidRutException(rutTexto,
                    "digito verificador incorrecto, se esperaba '" + digitoVerificadorEsperado + "'");
        }
        return new Rut(numero, digitoVerificadorIngresado);
    }

    /**
     * Calcula el digito verificador segun el algoritmo oficial del Servicio de
     * Impuestos Internos (SII) de Chile, con serie de multiplicadores 2-7 y modulo 11.
     */
    private static char calcularDigitoVerificador(long numero) {
        int suma = 0;
        int multiplicador = 2;
        long resto = numero;
        while (resto > 0) {
            suma += (int) (resto % 10) * multiplicador;
            resto /= 10;
            multiplicador = (multiplicador == 7) ? 2 : multiplicador + 1;
        }
        int digitoCalculado = 11 - (suma % 11);
        return switch (digitoCalculado) {
            case 11 -> '0';
            case 10 -> 'K';
            default -> Character.forDigit(digitoCalculado, 10);
        };
    }

    public long numero() {
        return numero;
    }

    public char digitoVerificador() {
        return digitoVerificador;
    }

    /** Formato canonico de persistencia y comparacion: {@code 12345678-5}. */
    public String formatoCanonico() {
        return numero + "-" + digitoVerificador;
    }

    /** Formato de presentacion al usuario: {@code 12.345.678-5}. */
    public String formatoPresentacion() {
        String numeroTexto = String.valueOf(numero);
        StringBuilder conPuntos = new StringBuilder();
        int contador = 0;
        for (int i = numeroTexto.length() - 1; i >= 0; i--) {
            conPuntos.append(numeroTexto.charAt(i));
            contador++;
            if (contador % 3 == 0 && i != 0) {
                conPuntos.append('.');
            }
        }
        return conPuntos.reverse() + "-" + digitoVerificador;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Rut rut)) return false;
        return numero == rut.numero && digitoVerificador == rut.digitoVerificador;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numero, digitoVerificador);
    }

    @Override
    public int compareTo(Rut otro) {
        return Long.compare(this.numero, otro.numero);
    }

    @Override
    public String toString() {
        return formatoCanonico();
    }
}
