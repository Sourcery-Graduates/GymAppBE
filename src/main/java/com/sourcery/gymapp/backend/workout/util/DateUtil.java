package com.sourcery.gymapp.backend.workout.util;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class DateUtil {
    public static long calculateDaysSince(ZonedDateTime dateTime) {
        ZonedDateTime now = ZonedDateTime.now();

        return ChronoUnit.DAYS.between(dateTime, now);
    }
}
