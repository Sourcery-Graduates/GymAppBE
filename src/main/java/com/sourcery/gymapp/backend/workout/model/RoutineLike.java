package com.sourcery.gymapp.backend.workout.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "routine_like", schema = "workout_data")
@EntityListeners(AuditingEntityListener.class)
public class RoutineLike{

    @EmbeddedId
    private RoutineLikeId id;

    @MapsId("user_id")
    @Column(name = "user_id", insertable = false, updatable = false)
    private UUID userId;

    @MapsId("routine_id")
    @Column(name = "routine_id", insertable = false, updatable = false)
    private UUID routineId;

    @Column(updatable = false, nullable = false)
    @CreatedDate
    protected LocalDateTime createdAt;

    @Column(nullable = false)
    @LastModifiedDate
    protected LocalDateTime modifiedAt;

    @Column(updatable = false, nullable = false)
    @CreatedBy
    protected UUID createdBy;

    @Column(nullable = false)
    @LastModifiedBy
    protected UUID modifiedBy;
}
