package com.sourcery.gymapp.backend.workout.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MuscleSetDto {
    private UUID workoutExerciseId;
    private Integer numberOfSets;
    private List<String> primaryMuscles;
}