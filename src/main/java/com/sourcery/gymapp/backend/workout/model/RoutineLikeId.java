package com.sourcery.gymapp.backend.workout.model;

import java.io.Serializable;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.UUID;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class RoutineLikeId implements Serializable {

    private UUID userId;
    private UUID routineId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoutineLikeId that = (RoutineLikeId) o;
        return userId.equals(that.userId) && routineId.equals(that.routineId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, routineId);
    }
}
