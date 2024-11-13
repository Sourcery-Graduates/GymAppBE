package com.sourcery.gymapp.backend.workout.dto;

import java.util.UUID;

public record ResponseLikeCountDto(
        UUID routineId,
        long likeCount
) {
}
