package com.sourcery.gymapp.backend.workout.repository;

import com.sourcery.gymapp.backend.workout.model.Workout;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface WorkoutRepository extends JpaRepository<Workout, UUID> {

    List<Workout> findByUserId(UUID userId, Sort sort);

    List<Workout> findByUserIdAndDateBetween(UUID userId, ZonedDateTime startDate,
                                             ZonedDateTime endDate, Sort sort);

    @Query(
            "SELECT COUNT(w) " +
            "FROM Workout w " +
            "WHERE (w.date BETWEEN :startOfTheMonth AND :endOfTheMonth) " +
            "AND (w.userId = :currentUserId)"
    )
    int countWorkoutsByUserIdAndDateBetween(UUID currentUserId,
                                            ZonedDateTime startOfTheMonth,
                                            ZonedDateTime endOfTheMonth);

    @Query(
            "SELECT SUM(wes.setNumber * wes.reps * wes.weight) as total_weight, w.userId " +
            "FROM WorkoutExerciseSet wes " +
            "LEFT JOIN WorkoutExercise we " +
            "ON wes.workoutExercise.id = we.id " +
            "JOIN Workout w " +
            "ON we.workout.id = w.id " +
            "WHERE (w.date BETWEEN :startOfTheMonth AND :endOfTheMonth) " +
            "AND w.userId = :currentUserId " +
            "GROUP BY w.userId"
    )
    Integer getTotalWeightByUserIdAndDateBetween(UUID currentUserId,
                                              ZonedDateTime startOfTheMonth,
                                              ZonedDateTime endOfTheMonth);
}
