package com.sourcery.gymapp.backend.workout.repository;

import com.sourcery.gymapp.backend.workout.repository.projection.RoutineLikeCountProjection;
import com.sourcery.gymapp.backend.workout.model.RoutineLike;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RoutineLikeRepository extends JpaRepository<RoutineLike, UUID> {

    boolean existsByRoutineIdAndUserId(UUID routineId, UUID userId);

    long countByRoutineId(UUID routineId);

    void deleteByRoutineIdAndUserId(UUID routineId, UUID userId);

    @Query("SELECT rl.routineId AS routineId, COUNT(rl) AS likeCount " +
            "FROM RoutineLike rl WHERE rl.routineId IN :routineIds GROUP BY rl.routineId")
    List<RoutineLikeCountProjection> findLikesCountsByRoutineIds(List<UUID> routineIds);

}
