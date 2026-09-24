package cl.tomed2016.smartlogistic.domain.calendar;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class BusinessCalendarTest {

    private final BusinessCalendar businessCalendar = new BusinessCalendar();

    @Test
    void shouldSkipWeekendWhenCalculatingNextWorkingDay() {
        LocalDate friday = LocalDate.of(2026, 9, 25);

        assertThat(businessCalendar.nextWorkingDay(friday)).isEqualTo(LocalDate.of(2026, 9, 28));
    }

    @Test
    void shouldTreatNationalHolidayAsNonWorkingDay() {
        LocalDate holidayEve = LocalDate.of(2026, 9, 17);

        assertThat(businessCalendar.nextWorkingDay(holidayEve)).isEqualTo(LocalDate.of(2026, 9, 21));
    }
}
