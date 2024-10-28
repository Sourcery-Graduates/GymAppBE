package com.sourcery.gymapp.backend.workout.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "routine_like", schema = "workout_data")
public class RoutineLike {

    @EmbeddedId
    private RoutineLikeId id;

    @MapsId("userId")
    private UUID userId;

    @OneToOne
    @MapsId("routineId")
    @JoinColumn(name = "routineId")
    private Routine routine;
}
