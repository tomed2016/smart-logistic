package cl.smartlogistic.customer.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegionTest {

    @ParameterizedTest(name = "codigo INE {0} resuelve a {1}")
    @CsvSource({
            "1, TARAPACA",
            "2, ANTOFAGASTA",
            "3, ATACAMA",
            "4, COQUIMBO",
            "5, VALPARAISO",
            "6, LIBERTADOR_GENERAL_BERNARDO_OHIGGINS",
            "7, MAULE",
            "8, BIOBIO",
            "9, LA_ARAUCANIA",
            "10, LOS_LAGOS",
            "11, AYSEN_DEL_GENERAL_CARLOS_IBANEZ_DEL_CAMPO",
            "12, MAGALLANES_Y_ANTARTICA_CHILENA",
            "13, METROPOLITANA_DE_SANTIAGO",
            "14, LOS_RIOS",
            "15, ARICA_Y_PARINACOTA",
            "16, NUBLE",
    })
    @DisplayName("porCodigoIne resuelve cada uno de los 16 codigos oficiales")
    void porCodigoIneResuelveTodosLosCodigosOficiales(int codigoIne, Region esperado) {
        assertThat(Region.porCodigoIne(codigoIne)).isEqualTo(esperado);
    }

    @Test
    @DisplayName("porCodigoIne lanza excepcion para codigos fuera de rango")
    void porCodigoIneRechazaCodigoInvalido() {
        assertThatThrownBy(() -> Region.porCodigoIne(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("0");

        assertThatThrownBy(() -> Region.porCodigoIne(17))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("17");
    }
}
