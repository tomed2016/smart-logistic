package cl.tomed2016.smartlogistic.infrastructure.config;

import cl.tomed2016.smartlogistic.domain.calendar.BusinessCalendar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class PlatformProperties {

    @Bean
    Clock chileClock() {
        return Clock.system(BusinessCalendar.CHILE_ZONE_ID);
    }

    @Bean
    BusinessCalendar businessCalendar() {
        return new BusinessCalendar();
    }
}
