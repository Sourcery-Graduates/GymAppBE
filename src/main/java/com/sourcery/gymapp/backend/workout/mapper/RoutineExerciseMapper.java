package com.sourcery.gymapp.backend.workout.mapper;

import com.sourcery.gymapp.backend.workout.dto.CreateRoutineExerciseDto;
import com.sourcery.gymapp.backend.workout.dto.ExerciseSimpleDto;
import com.sourcery.gymapp.backend.workout.dto.ResponseRoutineExerciseDto;
import com.sourcery.gymapp.backend.workout.model.Exercise;
import com.sourcery.gymapp.backend.workout.model.Routine;
import com.sourcery.gymapp.backend.workout.model.RoutineExercise;
import org.springframework.stereotype.Component;

@Component
public class RoutineExerciseMapper {

    public RoutineExercise toEntity(
            CreateRoutineExerciseDto createRoutineExerciseDto,
            Routine routine,
            Exercise exercise) {

        RoutineExercise routineExercise = new RoutineExercise();
        routineExercise.setId(createRoutineExerciseDto.exerciseId());
        routineExercise.setExercise(exercise);
        routineExercise.setRoutine(routine);
        routineExercise.setOrderNumber(createRoutineExerciseDto.orderNumber());
        routineExercise.setDefaultSets(createRoutineExerciseDto.defaultSets());
        routineExercise.setDefaultReps(createRoutineExerciseDto.defaultReps());
        routineExercise.setDefaultWeight(createRoutineExerciseDto.defaultWeight());
        routineExercise.setDefaultRestTime(createRoutineExerciseDto.defaultRestTime());
        routineExercise.setWeightUnit(createRoutineExerciseDto.weightUnit());
        routineExercise.setRestTimeUnit(createRoutineExerciseDto.restTimeUnit());
        return routineExercise;
    }

    public CreateRoutineExerciseDto toCreateRoutineExerciseDto(RoutineExercise routineExercise) {
        return new CreateRoutineExerciseDto(
                routineExercise.getId(),
                routineExercise.getOrderNumber(),
                routineExercise.getDefaultSets(),
                routineExercise.getDefaultReps(),
                routineExercise.getDefaultWeight(),
                routineExercise.getDefaultRestTime(),
                routineExercise.getWeightUnit(),
                routineExercise.getRestTimeUnit(),
                routineExercise.getNotes()
        );
    }

    public ResponseRoutineExerciseDto toResponseRoutineExerciseDto(RoutineExercise routineExercise) {
        ExerciseSimpleDto exerciseSimpleDto = new ExerciseSimpleDto(
                routineExercise.getExercise().getId(),
                routineExercise.getExercise().getName()
        );

        return new ResponseRoutineExerciseDto(
                routineExercise.getId(),
                exerciseSimpleDto,
                routineExercise.getOrderNumber(),
                routineExercise.getDefaultSets(),
                routineExercise.getDefaultReps(),
                routineExercise.getDefaultWeight(),
                routineExercise.getDefaultRestTime(),
                routineExercise.getWeightUnit(),
                routineExercise.getRestTimeUnit(),
                routineExercise.getNotes()
        );
    }
}
