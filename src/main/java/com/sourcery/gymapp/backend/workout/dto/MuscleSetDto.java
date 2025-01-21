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
    private List<String> primaryMuscles;
    private Long numberOfSets;
}