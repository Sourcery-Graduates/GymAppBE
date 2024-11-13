package com.sourcery.gymapp.backend.workout.factory;


import com.sourcery.gymapp.backend.workout.dto.CreateRoutineDto;
import com.sourcery.gymapp.backend.workout.dto.ResponseRoutineDto;
import com.sourcery.gymapp.backend.workout.model.Routine;

import java.time.LocalDateTime;
import java.util.UUID;

public class RoutineFactory {

    public static Routine createRoutine(String name, String description, LocalDateTime createdAt, UUID userId) {
        Routine routine = new Routine();

        routine.setId(UUID.randomUUID());
        routine.setName(name);
        routine.setDescription(description);
        routine.setCreatedAt(createdAt);
        routine.setUserId(userId);
        return routine;
    }

    public static Routine createRoutine(String name, String description) {
        return createRoutine(
                name,
                description,
                LocalDateTime.now(),
                UUID.randomUUID()
        );
    }

    public static Routine createRoutine(String name) {
        return createRoutine(
                name,
                "Test Description",
                LocalDateTime.now(),
                UUID.randomUUID()
        );
    }

    public static Routine createRoutine() {
        return createRoutine(
                "Test Routine",
                "Test Description",
                LocalDateTime.now(),
                UUID.randomUUID()
        );
    }

    public static CreateRoutineDto createRoutineDto(String name, String description) {
        return new CreateRoutineDto(
                name,
                description
        );
    }

    public static CreateRoutineDto createRoutineDto() {
        return createRoutineDto(
                "Test Routine",
                "Test Description"
        );
    }

    public static ResponseRoutineDto createResponseRoutineDto(UUID id, String name, String description, LocalDateTime createdAt, UUID userId, long likesCount) {
        return new ResponseRoutineDto(
                id,
                name,
                description,
                createdAt,
                userId,
                likesCount
        );
    }

    public static ResponseRoutineDto createResponseRoutineDto() {
        return createResponseRoutineDto(
                UUID.randomUUID(),
                "Test Routine",
                "Test Description",
                LocalDateTime.now(),
                UUID.fromString("00000000-0000-0000-0000-000000000000"),
                0L
        );
    }
}
