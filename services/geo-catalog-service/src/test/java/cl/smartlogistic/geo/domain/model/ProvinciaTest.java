package cl.smartlogistic.geo.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProvinciaTest {

    @Test
    void creaProvinciaValida() {
        assertThatCode(() -> new Provincia(131, "Santiago", 13)).doesNotThrowAnyException();
    }

    @Test
    void rechazaCodigoDeRegionNoPositivo() {
        assertThatThrownBy(() -> new Provincia(131, "Santiago", 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rechazaNombreEnBlanco() {
        assertThatThrownBy(() -> new Provincia(131, "", 13))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
