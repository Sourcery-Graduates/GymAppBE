package com.sourcery.gymapp.backend.workout.integration;

import com.sourcery.gymapp.backend.workout.dto.MuscleSetDto;
import com.sourcery.gymapp.backend.workout.factory.ExerciseFactory;
import com.sourcery.gymapp.backend.workout.factory.WorkoutExerciseFactory;
import com.sourcery.gymapp.backend.workout.factory.WorkoutExerciseSetFactory;
import com.sourcery.gymapp.backend.workout.factory.WorkoutFactory;
import com.sourcery.gymapp.backend.workout.model.Exercise;
import com.sourcery.gymapp.backend.workout.model.Workout;
import com.sourcery.gymapp.backend.workout.model.WorkoutExercise;
import com.sourcery.gymapp.backend.workout.model.WorkoutExerciseSet;
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
import java.util.Optional;
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
        userId = UUID.fromString("00000000-0000-0000-0000-000000000000");

        Exercise exercise = ExerciseFactory.createExercise(UUID.fromString("00000000-0000-0000-0000-000000000001"), "exercise");

        Exercise exerciseSaved = exerciseRepository.save(exercise);

        Workout workout1 = WorkoutFactory.createWorkout(userId, "workout1", ZonedDateTime.parse("2025-12-01T00:00:00Z"));
        Workout workout2 = WorkoutFactory.createWorkout(userId, "workout2", ZonedDateTime.parse("2025-12-02T00:00:00Z"));
        Workout workout3 = WorkoutFactory.createWorkout(userId, "workout3", ZonedDateTime.parse("2025-12-03T00:00:00Z"));

        List<Workout> workouts = workoutRepository.saveAll(List.of(workout1, workout2, workout3));

        WorkoutExercise workoutExercise1 = WorkoutExerciseFactory.createWorkoutExercise(exerciseSaved, workouts.getFirst());
        WorkoutExercise workoutExercise2 = WorkoutExerciseFactory.createWorkoutExercise(exerciseSaved, workouts.getFirst());
        WorkoutExercise workoutExercise3 = WorkoutExerciseFactory.createWorkoutExercise(exerciseSaved, workouts.getFirst());

        List<WorkoutExercise> workoutExercises = workoutExerciseRepository.saveAll(
                List.of(workoutExercise1, workoutExercise2, workoutExercise3));

        WorkoutExerciseSet workoutExerciseSet1 = WorkoutExerciseSetFactory.createWorkoutExerciseSet(workoutExercises.getFirst());
        WorkoutExerciseSet workoutExerciseSet2 = WorkoutExerciseSetFactory.createWorkoutExerciseSet(workoutExercises.getFirst());
        WorkoutExerciseSet workoutExerciseSet3 = WorkoutExerciseSetFactory.createWorkoutExerciseSet(workoutExercises.getFirst());

        WorkoutExerciseSet workoutExerciseSet4 = WorkoutExerciseSetFactory.createWorkoutExerciseSet(workoutExercises.get(1));
        WorkoutExerciseSet workoutExerciseSet5 = WorkoutExerciseSetFactory.createWorkoutExerciseSet(workoutExercises.get(1));
        WorkoutExerciseSet workoutExerciseSet6 = WorkoutExerciseSetFactory.createWorkoutExerciseSet(workoutExercises.get(1));

        WorkoutExerciseSet workoutExerciseSet7 = WorkoutExerciseSetFactory.createWorkoutExerciseSet(workoutExercises.get(2));
        WorkoutExerciseSet workoutExerciseSet8 = WorkoutExerciseSetFactory.createWorkoutExerciseSet(workoutExercises.get(2));
        WorkoutExerciseSet workoutExerciseSet9 = WorkoutExerciseSetFactory.createWorkoutExerciseSet(workoutExercises.get(2));

        workoutExerciseSetRepository.saveAll(List.of(
                workoutExerciseSet1, workoutExerciseSet2, workoutExerciseSet3,
                workoutExerciseSet4, workoutExerciseSet5, workoutExerciseSet6,
                workoutExerciseSet7, workoutExerciseSet8, workoutExerciseSet9
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
    void testCountWorkoutsByUserIdAndDateBetween_ShouldReturnZero() {
        int workoutCount = workoutRepository.countWorkoutsByUserIdAndDateBetween(userId,
                ZonedDateTime.parse("2025-01-01T00:00:00Z"),
                ZonedDateTime.parse("2025-01-31T00:00:00Z"));

        assertThat(workoutCount).isEqualTo(0);
    }


    @Test
    void testGetTotalWeightByUserIdAndDateBetween() {
        Optional<Integer> totalWeight = workoutRepository.getTotalWeightByUserIdAndDateBetween(userId,
                ZonedDateTime.parse("2025-12-01T00:00:00Z"),
                ZonedDateTime.parse("2025-12-31T00:00:00Z"));

        assertThat(totalWeight).isPresent();
        assertThat(totalWeight.get()).isEqualTo(4500);
    }

    @Test
    void testGetTotalWeightByUserIdAndDateBetween_shouldReturnEmpty() {
        Optional<Integer> totalWeight = workoutRepository.getTotalWeightByUserIdAndDateBetween(userId,
                ZonedDateTime.parse("2025-01-01T00:00:00Z"),
                ZonedDateTime.parse("2025-01-31T00:00:00Z"));

        assertThat(totalWeight).isEmpty();
    }

    @Test
    void testGetTotalMuscleSetsByUserIdAndDateBetween() {
        List<MuscleSetDto> muscleSets = workoutRepository.getTotalMuscleSetsByUserIdAndDateBetween(userId,
                ZonedDateTime.parse("2025-12-01T00:00:00Z"),
                ZonedDateTime.parse("2025-12-31T00:00:00Z"));

        assertThat(muscleSets).hasSize(1);
        assertThat(muscleSets).containsExactlyInAnyOrder(new MuscleSetDto(List.of("chest"), 9L));
    }
}
