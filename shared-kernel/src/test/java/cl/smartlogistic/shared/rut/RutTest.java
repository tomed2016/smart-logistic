package cl.smartlogistic.shared.rut;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RutTest {

    @ParameterizedTest(name = "{0} es un RUT valido")
    @CsvSource({
            "12345678-5",
            "12.345.678-5",
            "7.897.867-8",
            "7897867-8",
            "1000000-9",
            "22.222.222-2"
    })
    @DisplayName("Acepta RUTs validos en distintos formatos de entrada")
    void aceptaRutsValidos(String rutTexto) {
        Rut rut = Rut.of(rutTexto);
        assertThat(rut.formatoCanonico()).isNotBlank();
    }

    @Test
    @DisplayName("Calcula correctamente el digito verificador 'K'")
    void calculaDigitoVerificadorK() {
        Rut rut = Rut.of("6.000.000-K");
        assertThat(rut.digitoVerificador()).isEqualTo('K');
        assertThat(rut.numero()).isEqualTo(6000000L);
    }

    @Test
    @DisplayName("El formato canonico normaliza puntos y guion")
    void formatoCanonicoNormaliza() {
        Rut rut = Rut.of("12.345.678-5");
        assertThat(rut.formatoCanonico()).isEqualTo("12345678-5");
    }

    @Test
    @DisplayName("El formato de presentacion agrega puntos de miles")
    void formatoPresentacionAgregaPuntos() {
        Rut rut = Rut.of("12345678-5");
        assertThat(rut.formatoPresentacion()).isEqualTo("12.345.678-5");
    }

    @ParameterizedTest(name = "{0} es invalido")
    @ValueSource(strings = {"12345678-9", "abc-5", "1234", "", "12345678", "12345678-K5"})
    @DisplayName("Rechaza RUTs con digito verificador incorrecto o formato invalido")
    void rechazaRutsInvalidos(String rutTexto) {
        assertThatThrownBy(() -> Rut.of(rutTexto)).isInstanceOf(InvalidRutException.class);
    }

    @Test
    @DisplayName("Rechaza RUT nulo")
    void rechazaRutNulo() {
        assertThatThrownBy(() -> Rut.of(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Dos RUTs con el mismo numero y digito verificador son iguales")
    void igualdadPorValor() {
        assertThat(Rut.of("12.345.678-5")).isEqualTo(Rut.of("12345678-5"));
        assertThat(Rut.of("12.345.678-5")).hasSameHashCodeAs(Rut.of("12345678-5"));
    }

    @Test
    @DisplayName("RUTs se ordenan por numero ascendente")
    void ordenamientoPorNumero() {
        assertThat(Rut.of("1000000-9").compareTo(Rut.of("12345678-5"))).isNegative();
    }
}
