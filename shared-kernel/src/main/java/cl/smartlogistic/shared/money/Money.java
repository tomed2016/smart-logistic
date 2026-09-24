package cl.smartlogistic.shared.money;

import java.io.Serializable;
import java.util.Objects;

/**
 * Value Object monetario para Pesos Chilenos (CLP). El CLP no utiliza subunidades
 * fraccionarias en la operacion habitual del negocio, por lo que el monto se modela
 * como un entero de 64 bits (pesos completos), evitando los problemas de precision
 * de {@code double} sin la sobrecarga de {@code BigDecimal} para esta moneda.
 *
 * <p>Inmutable y siempre no-negativo salvo que se indique explicitamente lo
 * contrario mediante {@link #allowNegative()} (util para representar, por ejemplo,
 * saldos a favor del cliente).</p>
 */
public final class Money implements Serializable, Comparable<Money> {

    public static final String CLP = "CLP";

    private static final Money ZERO = new Money(0L);

    private final long montoEnPesos;

    private Money(long montoEnPesos) {
        this.montoEnPesos = montoEnPesos;
    }

    public static Money zero() {
        return ZERO;
    }

    /** Crea un monto no-negativo en pesos chilenos. */
    public static Money of(long montoEnPesos) {
        if (montoEnPesos < 0) {
            throw new IllegalArgumentException(
                    "El monto no puede ser negativo: " + montoEnPesos + ". Use allowNegative() si corresponde.");
        }
        return new Money(montoEnPesos);
    }

    /** Crea un monto que puede ser negativo (ej. ajustes, notas de credito). */
    public static Money allowNegative(long montoEnPesos) {
        return new Money(montoEnPesos);
    }

    public long montoEnPesos() {
        return montoEnPesos;
    }

    public String moneda() {
        return CLP;
    }

    public Money sumar(Money otro) {
        return new Money(Math.addExact(this.montoEnPesos, otro.montoEnPesos));
    }

    public Money restar(Money otro) {
        return new Money(Math.subtractExact(this.montoEnPesos, otro.montoEnPesos));
    }

    public Money multiplicar(long factor) {
        return new Money(Math.multiplyExact(this.montoEnPesos, factor));
    }

    public boolean esNegativo() {
        return montoEnPesos < 0;
    }

    public boolean esMayorQue(Money otro) {
        return this.montoEnPesos > otro.montoEnPesos;
    }

    @Override
    public int compareTo(Money otro) {
        return Long.compare(this.montoEnPesos, otro.montoEnPesos);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money money)) return false;
        return montoEnPesos == money.montoEnPesos;
    }

    @Override
    public int hashCode() {
        return Objects.hash(montoEnPesos);
    }

    @Override
    public String toString() {
        return "$" + montoEnPesos + " " + CLP;
    }
}
