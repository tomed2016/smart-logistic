package cl.smartlogistic.geo.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;

class RegionTest {

    @Test
    void creaRegionValida() {
        assertThatCode(() -> new Region(13, "Región Metropolitana de Santiago")).doesNotThrowAnyException();
    }

    @Test
    void rechazaNombreEnBlanco() {
        assertThatThrownBy(() -> new Region(13, " "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rechazaCodigoNoPositivo() {
        assertThatThrownBy(() -> new Region(0, "Valparaíso"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
