package com.sourcery.gymapp.backend.workout.service;

import com.sourcery.gymapp.backend.workout.exception.ExerciseNotFoundException;
import com.sourcery.gymapp.backend.workout.factory.ExerciseFactory;
import com.sourcery.gymapp.backend.workout.model.Exercise;
import com.sourcery.gymapp.backend.workout.repository.ExerciseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExerciseServiceTest {

    @Mock
    private ExerciseRepository exerciseRepository;

    @InjectMocks
    private ExerciseService exerciseService;

    private Exercise exercise1;
    private Exercise exercise2;
    private List<UUID> exerciseIds;

    @BeforeEach
    void setUp() {
        exercise1 = ExerciseFactory.createExercise();
        exercise2 = ExerciseFactory.createExercise();

        exerciseIds = List.of(exercise1.getId(), exercise2.getId());
    }

    @Test
    void shouldReturnExerciseMapWhenAllExercisesAreFound() {
        // Arrange
        when(exerciseRepository.findAllByIdIn(anyList())).thenReturn(List.of(exercise1, exercise2));

        // Act
        Map<UUID, Exercise> result = exerciseService.getExerciseMapByIds(exerciseIds);

        // Assert
        assertEquals(2, result.size());
        assertEquals(exercise1, result.get(exercise1.getId()));
        assertEquals(exercise2, result.get(exercise2.getId()));
    }

    @Test
    void shouldThrowExerciseNotFoundExceptionWhenSomeExercisesAreMissing() {
        // Arrange
        when(exerciseRepository.findAllByIdIn(anyList())).thenReturn(List.of(exercise1));

        // Act & Assert
        ExerciseNotFoundException exception = assertThrows(
                ExerciseNotFoundException.class,
                () -> exerciseService.getExerciseMapByIds(exerciseIds)
        );

        String expectedMessage = "Can't find Exercises by IDs [" + exercise2.getId() + "]";
        assertEquals(expectedMessage, exception.getMessage());
    }
}