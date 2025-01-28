package com.sourcery.gymapp.backend.workout.factory;

import com.sourcery.gymapp.backend.workout.dto.WorkoutStatsDto;

import java.util.UUID;

public class WorkoutStatsFactory {

    public static WorkoutStatsDto createWorkoutStatsDto(UUID id, String type, String content) {

        return new WorkoutStatsDto(
                id,
                type,
                content
        );
    }
}
