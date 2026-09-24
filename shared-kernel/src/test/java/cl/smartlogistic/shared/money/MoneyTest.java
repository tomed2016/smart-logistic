package cl.smartlogistic.shared.money;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MoneyTest {

    @Test
    @DisplayName("Money.zero() representa 0 CLP")
    void zeroEsCero() {
        assertThat(Money.zero().montoEnPesos()).isZero();
        assertThat(Money.zero().moneda()).isEqualTo("CLP");
    }

    @Test
    @DisplayName("No permite construir montos negativos con of()")
    void rechazaMontoNegativo() {
        assertThatThrownBy(() -> Money.of(-100)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("allowNegative() permite montos negativos explicitamente")
    void permiteNegativoExplicito() {
        Money saldoAFavor = Money.allowNegative(-5000);
        assertThat(saldoAFavor.esNegativo()).isTrue();
    }

    @Test
    @DisplayName("Suma y resta de montos")
    void sumaYResta() {
        Money a = Money.of(10_000);
        Money b = Money.of(3_000);
        assertThat(a.sumar(b).montoEnPesos()).isEqualTo(13_000);
        assertThat(a.restar(b).montoEnPesos()).isEqualTo(7_000);
    }

    @Test
    @DisplayName("Multiplicacion por cantidad (ej. precio unitario x cantidad de bidones)")
    void multiplicacionPorCantidad() {
        Money precioUnitario = Money.of(2_500);
        assertThat(precioUnitario.multiplicar(12).montoEnPesos()).isEqualTo(30_000);
    }

    @Test
    @DisplayName("Comparacion esMayorQue")
    void comparacion() {
        assertThat(Money.of(5_000).esMayorQue(Money.of(1_000))).isTrue();
        assertThat(Money.of(1_000).esMayorQue(Money.of(5_000))).isFalse();
    }

    @Test
    @DisplayName("Igualdad por valor")
    void igualdadPorValor() {
        assertThat(Money.of(1_000)).isEqualTo(Money.of(1_000));
        assertThat(Money.of(1_000)).hasSameHashCodeAs(Money.of(1_000));
    }
}
