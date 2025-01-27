package com.sourcery.gymapp.backend.workout.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OffsetDateService {
    private final Clock clock;

    /**
     * Returns the start and end of the week with an offset.
     * @param offsetWeek the number of weeks to offset from the current week.
     * @return a list containing the start and end of the week.
     */
    public List<ZonedDateTime> getWeeklyDateRangeOffset(Integer offsetWeek) {
        ZonedDateTime now = ZonedDateTime.now(clock);
        ZonedDateTime startOfWeek = now.minusWeeks(offsetWeek)
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .withHour(0).withMinute(0).withSecond(0).withNano(0);

        ZonedDateTime endOfWeek = startOfWeek.plusDays(7)
                .withHour(23).withMinute(59).withSecond(59).withNano(999999999);

        return List.of(startOfWeek, endOfWeek);
    }

    /**
     * Returns the start of the month with an offset and the end of the current month.
     * @param offsetStartMonth the number of months to offset from the current month.
     * @return a list containing the start of the offset month and the end of the current month.
     */
    public List<ZonedDateTime> getStartOffsetAndEndCurrentMonth(Integer offsetStartMonth) {
        ZonedDateTime startOfTheMonth = getOffsetStartMonthFromCurrentDate(offsetStartMonth);
        ZonedDateTime endOfTheMonth = getEndMonthFromCurrentDate();

        return List.of(startOfTheMonth, endOfTheMonth);
    }

    /**
     * Returns the start and end of the month with an offset.
     * @param offsetMonth the number of months to offset from the current month.
     * @return a list containing the start and end of the offset month.
     */
    public List<ZonedDateTime> getMonthlyDateRangeOffset(Integer offsetMonth) {
        ZonedDateTime startOfTheMonth = getOffsetStartMonthFromCurrentDate(offsetMonth);
        ZonedDateTime endOfTheMonth =  getOffsetEndMonthFromCurrentDate(offsetMonth);

        return List.of(startOfTheMonth, endOfTheMonth);
    }

    private ZonedDateTime getOffsetStartMonthFromCurrentDate(Integer offsetStartMonth) {
        ZonedDateTime currentDate = ZonedDateTime.now(clock);

        return currentDate
                .minusMonths(offsetStartMonth)
                .with(TemporalAdjusters.firstDayOfMonth())
                .withHour(0).withMinute(0).withSecond(0).withNano(0);
    }

    private ZonedDateTime getOffsetEndMonthFromCurrentDate(Integer offsetEndMonth) {
        ZonedDateTime currentDate = ZonedDateTime.now(clock);

        return currentDate
                .minusMonths(offsetEndMonth)
                .with(TemporalAdjusters.lastDayOfMonth())
                .withHour(23).withMinute(59).withSecond(59).withNano(999999999);
    }

    private ZonedDateTime getEndMonthFromCurrentDate() {
        ZonedDateTime currentDate = ZonedDateTime.now(clock);

        return currentDate
                .with(TemporalAdjusters.lastDayOfMonth())
                .withHour(23).withMinute(59).withSecond(59).withNano(999999999);
    }
}
