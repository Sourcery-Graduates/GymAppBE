package com.sourcery.gymapp.backend.workout.integration;

import com.sourcery.gymapp.backend.workout.factory.ExerciseFactory;
import com.sourcery.gymapp.backend.workout.factory.WorkoutExerciseFactory;
import com.sourcery.gymapp.backend.workout.factory.WorkoutExerciseSetFactory;
import com.sourcery.gymapp.backend.workout.factory.WorkoutFactory;
import com.sourcery.gymapp.backend.workout.model.Exercise;
import com.sourcery.gymapp.backend.workout.model.Workout;
import com.sourcery.gymapp.backend.workout.model.WorkoutExercise;
import com.sourcery.gymapp.backend.workout.repository.ExerciseRepository;
import com.sourcery.gymapp.backend.workout.repository.WorkoutExerciseRepository;
import com.sourcery.gymapp.backend.workout.repository.WorkoutExerciseSetRepository;
import com.sourcery.gymapp.backend.workout.repository.WorkoutRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class WorkoutRepositoryIntegrationTest extends BaseWorkoutIntegrationJPATest {

    @Autowired
    EntityManager entityManager;

    @Autowired
    WorkoutRepository workoutRepository;

    @Autowired
    ExerciseRepository exerciseRepository;

    @Autowired
    WorkoutExerciseRepository workoutExerciseRepository;

    @Autowired
    WorkoutExerciseSetRepository workoutExerciseSetRepository;

    UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.fromString("00000000-0000-0000-0000-000000000001");

        Exercise exercise1 = ExerciseFactory.createExercise(UUID.fromString("00000000-0000-0000-0000-000000000001"), "exercise1");
        Exercise exercise2 = ExerciseFactory.createExercise(UUID.fromString("00000000-0000-0000-0000-000000000002"), "exercise2");
        Exercise exercise3 = ExerciseFactory.createExercise(UUID.fromString("00000000-0000-0000-0000-000000000003"), "exercise3");

        exerciseRepository.saveAll(List.of(exercise1, exercise2, exercise3));

        Workout workout1 = WorkoutFactory.createWorkout(userId, "workout1", ZonedDateTime.parse("2025-12-01T00:00:00Z"));
        Workout workout2 = WorkoutFactory.createWorkout(userId, "workout2", ZonedDateTime.parse("2025-12-02T00:00:00Z"));
        Workout workout3 = WorkoutFactory.createWorkout(userId, "workout3", ZonedDateTime.parse("2025-12-03T00:00:00Z"));
        Workout workout4 = WorkoutFactory.createWorkout(userId, "workout4", ZonedDateTime.parse("2025-01-01T00:00:00Z"));
        Workout workout5 = WorkoutFactory.createWorkout(userId, "workout5", ZonedDateTime.parse("2025-01-02T00:00:00Z"));
        Workout workout6 = WorkoutFactory.createWorkout(userId, "workout6", ZonedDateTime.parse("2025-01-03T00:00:00Z"));

        workoutRepository.saveAll(List.of(workout1, workout2, workout3, workout4, workout5, workout6));

        WorkoutExercise workoutExercise1 = WorkoutExerciseFactory.createWorkoutExercise(exercise1, workout1);
        WorkoutExercise workoutExercise2 = WorkoutExerciseFactory.createWorkoutExercise(exercise2, workout2);
        WorkoutExercise workoutExercise3 = WorkoutExerciseFactory.createWorkoutExercise(exercise3, workout3);

        workoutExerciseRepository.saveAll(List.of(workoutExercise1, workoutExercise2, workoutExercise3));

        workoutExerciseSetRepository.saveAll(List.of(
                WorkoutExerciseSetFactory.createWorkoutExerciseSet(workoutExercise1),
                WorkoutExerciseSetFactory.createWorkoutExerciseSet(workoutExercise2),
                WorkoutExerciseSetFactory.createWorkoutExerciseSet(workoutExercise3)
        ));
    }

    @Test
    void testCountWorkoutsByUserIdAndDateBetween() {
        int workoutCount = workoutRepository.countWorkoutsByUserIdAndDateBetween(userId,
                ZonedDateTime.parse("2025-12-01T00:00:00Z"),
                ZonedDateTime.parse("2025-12-31T00:00:00Z"));

        assertThat(workoutCount).isEqualTo(3);
    }

    @Test
    void testGetTotalWeightByUserIdAndDateBetween_shouldReturnZero() {
        int totalWeight = workoutRepository.getTotalWeightByUserIdAndDateBetween(userId,
                ZonedDateTime.parse("2025-12-01T00:00:00Z"),
                ZonedDateTime.parse("2025-12-31T00:00:00Z"));

        assertThat(totalWeight).isEqualTo(0);
    }

    @Test
    void testGetTotalWeightByUserIdAndDateBetween() {
        int totalWeight = workoutRepository.getTotalWeightByUserIdAndDateBetween(userId,
                ZonedDateTime.parse("2025-01-01T00:00:00Z"),
                ZonedDateTime.parse("2025-01-31T00:00:00Z"));

        assertThat(totalWeight).isEqualTo(300);
    }
}
