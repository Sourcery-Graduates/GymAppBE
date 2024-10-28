package com.sourcery.gymapp.backend.workout.dto;
import java.util.UUID;

public record ResponseRoutineLikeDto(
    UUID routineId,

    UUID userId
) {
}
