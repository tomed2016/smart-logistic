package cl.smartlogistic.geo.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ComunaTest {

    @Test
    void creaComunaValida() {
        assertThatCode(() -> new Comuna(13119, "Maipú", 131, 13)).doesNotThrowAnyException();
    }

    @Test
    void rechazaCodigoDeProvinciaNoPositivo() {
        assertThatThrownBy(() -> new Comuna(13119, "Maipú", 0, 13))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rechazaCodigoDeRegionNoPositivo() {
        assertThatThrownBy(() -> new Comuna(13119, "Maipú", 131, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rechazaNombreNulo() {
        assertThatThrownBy(() -> new Comuna(13119, null, 131, 13))
                .isInstanceOf(NullPointerException.class);
    }
}
