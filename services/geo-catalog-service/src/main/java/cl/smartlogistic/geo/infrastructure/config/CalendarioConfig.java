package cl.smartlogistic.geo.infrastructure.config;

import cl.smartlogistic.shared.calendar.CalendarioChileno;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@code shared-kernel} es deliberadamente libre de dependencias de framework
 * (ver {@code shared-kernel/pom.xml}), por lo que sus clases no llevan anotaciones
 * de Spring. Este servicio expone {@link CalendarioChileno} como bean para poder
 * inyectarlo en la capa de aplicacion.
 */
@Configuration
public class CalendarioConfig {

    @Bean
    public CalendarioChileno calendarioChileno() {
        return new CalendarioChileno();
    }
}
