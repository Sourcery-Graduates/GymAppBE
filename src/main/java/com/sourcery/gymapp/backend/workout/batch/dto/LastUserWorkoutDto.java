package com.sourcery.gymapp.backend.workout.batch.dto;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
public class LastUserWorkoutDto {
    private UUID userId;
    private ZonedDateTime dateTime;
}