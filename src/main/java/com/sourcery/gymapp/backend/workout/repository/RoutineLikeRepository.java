package com.sourcery.gymapp.backend.workout.repository;

import com.sourcery.gymapp.backend.workout.model.RoutineLike;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoutineLikeRepository extends JpaRepository<RoutineLike, UUID> {

   Optional<RoutineLike> findByRoutineIdAndUserId(UUID routineId, UUID currentUserId);
}
