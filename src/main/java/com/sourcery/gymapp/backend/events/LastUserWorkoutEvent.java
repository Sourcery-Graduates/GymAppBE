package com.sourcery.gymapp.backend.events;

import java.time.ZonedDateTime;
import java.util.UUID;

public record LastUserWorkoutEvent(
        UUID userId,
        ZonedDateTime dateTime,
        Long daysSinceLastWorkout
) {
}
