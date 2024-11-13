package com.sourcery.gymapp.backend.workout.service;

import com.sourcery.gymapp.backend.workout.dto.ResponseLikeCountDto;
import com.sourcery.gymapp.backend.workout.exception.UserNotFoundException;
import com.sourcery.gymapp.backend.workout.model.RoutineLike;
import com.sourcery.gymapp.backend.workout.repository.RoutineLikeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RoutineLikeServiceTest {

    @Mock
    private RoutineLikeRepository routineLikeRepository;

    @Mock
    private WorkoutCurrentUserService currentUserService;

    @InjectMocks
    private RoutineLikeService routineLikeService;

    private UUID routineId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        routineId = UUID.randomUUID();
        userId = UUID.randomUUID();
    }

    @Test
    void addLikeToRoutine_ShouldAddLike_WhenUserIsNotNullAndLikeDoesNotExist() {
        when(currentUserService.getCurrentUserId()).thenReturn(userId);
        when(routineLikeRepository.existsByRoutineIdAndUserId(routineId, userId)).thenReturn(false);

        routineLikeService.addLikeToRoutine(routineId);

        verify(routineLikeRepository).save(any(RoutineLike.class));
    }

    @Test
    void addLikeToRoutine_ShouldNotAddLike_WhenUserAlreadyLikedRoutine() {
        when(currentUserService.getCurrentUserId()).thenReturn(userId);
        when(routineLikeRepository.existsByRoutineIdAndUserId(routineId, userId)).thenReturn(true);

        routineLikeService.addLikeToRoutine(routineId);

        verify(routineLikeRepository, never()).save(any(RoutineLike.class));
    }

    @Test
    void addLikeToRoutine_ShouldThrowUserNotFoundException_WhenUserIsNull() {
        when(currentUserService.getCurrentUserId()).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> routineLikeService.addLikeToRoutine(routineId));

        verify(routineLikeRepository, never()).save(any(RoutineLike.class));
    }

    @Test
    void removeLikeFromRoutine_ShouldRemoveLike_WhenUserIsNotNullAndLikeExists() {
        when(currentUserService.getCurrentUserId()).thenReturn(userId);
        when(routineLikeRepository.existsByRoutineIdAndUserId(routineId, userId)).thenReturn(true);

        routineLikeService.removeLikeFromRoutine(routineId);

        verify(routineLikeRepository).deleteByRoutineIdAndUserId(routineId, userId);
    }

    @Test
    void removeLikeFromRoutine_ShouldNotRemoveLike_WhenUserHasNotLikedRoutine() {
        when(currentUserService.getCurrentUserId()).thenReturn(userId);
        when(routineLikeRepository.existsByRoutineIdAndUserId(routineId, userId)).thenReturn(false);

        routineLikeService.removeLikeFromRoutine(routineId);

        verify(routineLikeRepository, never()).deleteByRoutineIdAndUserId(routineId, userId);
    }

    @Test
    void removeLikeFromRoutine_ShouldThrowUserNotFoundException_WhenUserIsNull() {
        when(currentUserService.getCurrentUserId()).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> routineLikeService.removeLikeFromRoutine(routineId));

        verify(routineLikeRepository, never()).deleteByRoutineIdAndUserId(any(UUID.class), any(UUID.class));
    }

    @Test
    void getRoutineLikes_ShouldReturnLikeCount() {
        long expectedCount = 5;
        when(routineLikeRepository.countByRoutineId(routineId)).thenReturn(expectedCount);

        ResponseLikeCountDto response = routineLikeService.getRoutineLikes(routineId);

        assertEquals(routineId, response.routineId());
        assertEquals(expectedCount, response.likeCount());
    }
}
