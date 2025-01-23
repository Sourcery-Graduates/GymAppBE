package com.sourcery.gymapp.backend.workout.util;

import lombok.experimental.UtilityClass;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@UtilityClass
public class OffsetDateUtil {
    public List<ZonedDateTime> getWeeklyDateRangeOffset(Integer offsetWeek) {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime startOfWeek = now.minusWeeks(offsetWeek)
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .withHour(0).withMinute(0).withSecond(0).withNano(0);

        ZonedDateTime endOfWeek = startOfWeek.plusDays(6)
                .withHour(23).withMinute(59).withSecond(59).withNano(999999999);

        return List.of(startOfWeek, endOfWeek);
    }


    public List<ZonedDateTime> getStartOffsetAndEndCurrentMonth(Integer offsetStartMonth) {
        ZonedDateTime currentDate = ZonedDateTime.now();

        ZonedDateTime startOfTheMonth = OffsetDateUtil.getOffsetStartMonthFromCurrentDate(offsetStartMonth, currentDate);
        ZonedDateTime endOfTheMonth = OffsetDateUtil.getEndMonthFromCurrentDate(currentDate);

        return List.of(startOfTheMonth, endOfTheMonth);
    }

    public List<ZonedDateTime> getMonthlyDateRangeOffset(Integer offsetMonth) {
        ZonedDateTime currentDate = ZonedDateTime.now();
        ZonedDateTime startOfTheMonth = currentDate.withDayOfMonth(1).withHour(0);
        ZonedDateTime endOfTheMonth = currentDate;

        if (offsetMonth != null) {
            startOfTheMonth = OffsetDateUtil.getOffsetStartMonthFromCurrentDate(offsetMonth, currentDate);

            endOfTheMonth = OffsetDateUtil.getOffsetEndMonthFromCurrentDate(offsetMonth, currentDate);
        }

        return List.of(startOfTheMonth, endOfTheMonth);
    }

    private static ZonedDateTime getOffsetStartMonthFromCurrentDate(Integer offsetStartMonth,
                                                             ZonedDateTime currentDate) {
        return currentDate.minusMonths(offsetStartMonth)
                .with(TemporalAdjusters.firstDayOfMonth())
                .withHour(0).withMinute(0).withSecond(0).withNano(0);
    }

    private static ZonedDateTime getOffsetEndMonthFromCurrentDate(Integer offsetEndMonth,
                                                           ZonedDateTime currentDate) {
        return currentDate.minusMonths(offsetEndMonth)
                .with(TemporalAdjusters.lastDayOfMonth())
                .withHour(23).withMinute(59).withSecond(59).withNano(999999999);
    }

    private static ZonedDateTime getEndMonthFromCurrentDate(ZonedDateTime currentDate) {
        return currentDate
                .with(TemporalAdjusters.lastDayOfMonth())
                .withHour(23).withMinute(59).withSecond(59).withNano(999999999);
    }
}
