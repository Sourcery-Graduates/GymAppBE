package com.sourcery.gymapp.backend.events;

import java.time.ZonedDateTime;
import java.util.UUID;

public record LastUserWorkoutWithEmailEvent(
        UUID userId,
        ZonedDateTime dateTime,
        String userEmail
) {
}
