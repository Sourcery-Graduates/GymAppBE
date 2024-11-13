package com.sourcery.gymapp.backend.workout.repository;

import com.sourcery.gymapp.backend.workout.model.RoutineLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RoutineLikeRepository extends JpaRepository<RoutineLike, UUID> {

    boolean existsByRoutineIdAndUserId(UUID routineId, UUID userId);

    long countByRoutineId(UUID routineId);

    void deleteByRoutineIdAndUserId(UUID routineId, UUID userId);
}
