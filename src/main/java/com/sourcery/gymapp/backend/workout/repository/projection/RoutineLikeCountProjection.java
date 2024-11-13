package com.sourcery.gymapp.backend.workout.repository.projection;

import java.util.UUID;

public interface RoutineLikeCountProjection {
    UUID getRoutineId();
    long getLikeCount();
}
