package com.sourcery.gymapp.backend.workout.exception;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class UserRoutineLikeNotFound extends WorkoutRuntimeException {

    public UserRoutineLikeNotFound(UUID routineId) {
        super("Can't find user's like by Routine by ID [%s]".formatted(routineId),
                ErrorCode.USER_ROUTINE_LIKE_NOT_FOUND,
                HttpStatus.NOT_FOUND);
    }
}
