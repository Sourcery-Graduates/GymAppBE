package com.sourcery.gymapp.backend.workout.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.annotation.CreatedDate;

@Getter
@Setter
@Entity
@Table(name = "routine_like", schema = "workout_data", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"routine_id", "user_id"})
})
public class RoutineLike {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "routine_id", nullable = false)
    private UUID routineId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "created_at", nullable = false)
    @CreatedDate
    private LocalDateTime createdAt = LocalDateTime.now();
}
