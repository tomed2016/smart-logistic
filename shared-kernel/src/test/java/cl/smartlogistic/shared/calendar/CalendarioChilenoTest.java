package cl.smartlogistic.shared.calendar;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CalendarioChilenoTest {

    private final CalendarioChileno calendario = new CalendarioChileno();

    @ParameterizedTest(name = "Domingo de Pascua {0} = {1}-{2}-{3}")
    @CsvSource({
            "2023,4,9",
            "2024,3,31",
            "2025,4,20",
            "2026,4,5",
            "2027,3,28"
    })
    void calculaDomingoDePascuaCorrectamente(int anio, int mes, int dia) {
        assertThat(CalendarioChileno.domingoDePascua(anio)).isEqualTo(LocalDate.of(anio, mes, dia));
    }

    @Test
    void incluyeViernesYSabadoSantoEnBaseAlDomingoDePascua() {
        List<Feriado> feriados = calendario.feriadosDelAnio(2024);
        LocalDate domingoPascua = LocalDate.of(2024, Month.MARCH, 31);

        assertThat(feriados).anySatisfy(f -> {
            assertThat(f.fecha()).isEqualTo(domingoPascua.minusDays(2));
            assertThat(f.nombre()).isEqualTo("Viernes Santo");
            assertThat(f.tipo()).isEqualTo(TipoFeriado.MOVIL_PASCUA);
        });
        assertThat(feriados).anySatisfy(f -> {
            assertThat(f.fecha()).isEqualTo(domingoPascua.minusDays(1));
            assertThat(f.nombre()).isEqualTo("Sábado Santo");
        });
    }

    @Test
    void incluyeLosFeriadosFijosDelAnio() {
        List<Feriado> feriados = calendario.feriadosDelAnio(2025);
        List<LocalDate> fechas = feriados.stream().map(Feriado::fecha).toList();

        assertThat(fechas).contains(
                LocalDate.of(2025, Month.JANUARY, 1),
                LocalDate.of(2025, Month.MAY, 1),
                LocalDate.of(2025, Month.MAY, 21),
                LocalDate.of(2025, Month.JULY, 16),
                LocalDate.of(2025, Month.AUGUST, 15),
                LocalDate.of(2025, Month.SEPTEMBER, 18),
                LocalDate.of(2025, Month.SEPTEMBER, 19),
                LocalDate.of(2025, Month.OCTOBER, 31),
                LocalDate.of(2025, Month.NOVEMBER, 1),
                LocalDate.of(2025, Month.DECEMBER, 8),
                LocalDate.of(2025, Month.DECEMBER, 25)
        );
    }

    @Test
    void trasladaSanPedroYSanPabloAlLunesAnteriorCuandoCaeMiercoles() {
        // 29 de junio de 2022 fue miercoles -> se traslada al lunes 27.
        assertThat(calendario.feriadosDelAnio(2022))
                .anySatisfy(f -> {
                    assertThat(f.nombre()).isEqualTo("San Pedro y San Pablo");
                    assertThat(f.fecha()).isEqualTo(LocalDate.of(2022, Month.JUNE, 27));
                    assertThat(f.tipo()).isEqualTo(TipoFeriado.MOVIL_LEY_19668);
                });
    }

    @Test
    void trasladaEncuentroDeDosMundosAlLunesSiguienteCuandoCaeViernes() {
        // 12 de octubre de 2029 cae viernes -> se traslada al lunes 15.
        assertThat(calendario.feriadosDelAnio(2029))
                .anySatisfy(f -> {
                    assertThat(f.nombre()).isEqualTo("Encuentro de Dos Mundos");
                    assertThat(f.fecha()).isEqualTo(LocalDate.of(2029, Month.OCTOBER, 15));
                    assertThat(f.tipo()).isEqualTo(TipoFeriado.MOVIL_LEY_19668);
                });
    }

    @Test
    void noTrasladaSanPedroYSanPabloCuandoCaeLunes() {
        // 29 de junio de 2026 cae lunes -> sin traslado.
        assertThat(calendario.feriadosDelAnio(2026))
                .anySatisfy(f -> {
                    assertThat(f.nombre()).isEqualTo("San Pedro y San Pablo");
                    assertThat(f.fecha()).isEqualTo(LocalDate.of(2026, Month.JUNE, 29));
                    assertThat(f.tipo()).isEqualTo(TipoFeriado.FIJO);
                });
    }

    @Test
    void esFeriadoDetectaAnioNuevo() {
        assertThat(calendario.esFeriado(LocalDate.of(2025, Month.JANUARY, 1))).isTrue();
        assertThat(calendario.esFeriado(LocalDate.of(2025, Month.JANUARY, 2))).isFalse();
    }

    @Test
    void esDiaHabilExcluyeFinDeSemanaYFeriados() {
        // Navidad 2025 cae jueves.
        assertThat(calendario.esDiaHabil(LocalDate.of(2025, Month.DECEMBER, 25))).isFalse();
        // Sabado.
        assertThat(calendario.esDiaHabil(LocalDate.of(2025, Month.DECEMBER, 27))).isFalse();
        // Un martes cualquiera sin feriado.
        assertThat(calendario.esDiaHabil(LocalDate.of(2025, Month.DECEMBER, 30))).isTrue();
    }

    @Test
    void siguienteDiaHabilSaltaFinDeSemanaYFeriadoConsecutivos() {
        // Miercoles 24 dic 2025 -> jueves 25 (Navidad, feriado) -> viernes 26 (habil).
        LocalDate resultado = calendario.siguienteDiaHabil(LocalDate.of(2025, Month.DECEMBER, 24));
        assertThat(resultado).isEqualTo(LocalDate.of(2025, Month.DECEMBER, 26));
    }

    @Test
    void siguienteDiaHabilSiempreAvanzaAlMenosUnDia() {
        // Aunque 'desde' ya sea habil, el resultado debe ser estrictamente posterior.
        LocalDate lunesHabil = LocalDate.of(2025, Month.DECEMBER, 29);
        LocalDate resultado = calendario.siguienteDiaHabil(lunesHabil);
        assertThat(resultado).isAfter(lunesHabil);
    }

    @Test
    void diaHabilMasCercanoRetornaLaMismaFechaSiYaEsHabil() {
        LocalDate martesHabil = LocalDate.of(2025, Month.DECEMBER, 30);
        assertThat(calendario.diaHabilMasCercano(martesHabil)).isEqualTo(martesHabil);
    }

    @Test
    void feriadosAdHocSeIncluyenSoloParaElAnioIndicadoYNoAfectanOtrosAnios() {
        LocalDate feriadoPuente = LocalDate.of(2025, Month.SEPTEMBER, 19 + 1); // 20-sep-2025, ficticio para la prueba
        CalendarioChileno conAdHoc = new CalendarioChileno(Set.of(feriadoPuente));

        assertThat(conAdHoc.esFeriado(feriadoPuente)).isTrue();
        assertThat(conAdHoc.feriadosDelAnio(2024)).noneMatch(f -> f.tipo() == TipoFeriado.AD_HOC);
        assertThat(new CalendarioChileno().esFeriado(feriadoPuente)).isFalse();
    }
}
