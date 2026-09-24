package cl.tomed2016.smartlogistic.domain.calendar;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.ZoneId;
import java.util.Objects;
import java.util.Set;

public class BusinessCalendar {

    public static final ZoneId CHILE_ZONE_ID = ZoneId.of("America/Santiago");

    private static final Set<MonthDay> FIXED_HOLIDAYS = Set.of(
            MonthDay.of(1, 1),
            MonthDay.of(5, 1),
            MonthDay.of(9, 18),
            MonthDay.of(9, 19),
            MonthDay.of(12, 25)
    );

    public boolean isWorkingDay(LocalDate date) {
        Objects.requireNonNull(date, "date must not be null");
        return !isWeekend(date) && !isHoliday(date);
    }

    public LocalDate nextWorkingDay(LocalDate date) {
        Objects.requireNonNull(date, "date must not be null");
        LocalDate candidate = date.plusDays(1);
        while (!isWorkingDay(candidate)) {
            candidate = candidate.plusDays(1);
        }
        return candidate;
    }

    private boolean isWeekend(LocalDate date) {
        return date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY;
    }

    private boolean isHoliday(LocalDate date) {
        return FIXED_HOLIDAYS.contains(MonthDay.from(date));
    }
}
