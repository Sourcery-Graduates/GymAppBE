package com.sourcery.gymapp.backend.workout.dto;

import lombok.AllArgsConstructor;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class MuscleSetDto {
    private UUID workoutExerciseId;
    private Integer numberOfSets;
    private List<String> primaryMuscles;
}