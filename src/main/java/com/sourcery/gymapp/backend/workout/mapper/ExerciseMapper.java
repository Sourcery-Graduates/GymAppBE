package com.sourcery.gymapp.backend.workout.mapper;

import com.sourcery.gymapp.backend.workout.dto.ExerciseDetailDto;
import com.sourcery.gymapp.backend.workout.model.Exercise;
import org.springframework.stereotype.Component;

@Component
public class ExerciseMapper {

    public ExerciseDetailDto toDto(Exercise exercise) {
        return new ExerciseDetailDto(
                exercise.getId(),
                exercise.getName(),
                exercise.getForce(),
                exercise.getLevel(),
                exercise.getMechanic(),
                exercise.getEquipment(),
                exercise.getPrimaryMuscles(),
                exercise.getSecondaryMuscles(),
                exercise.getDescription(),
                exercise.getCategory(),
                exercise.getImages()
        );
    }
}