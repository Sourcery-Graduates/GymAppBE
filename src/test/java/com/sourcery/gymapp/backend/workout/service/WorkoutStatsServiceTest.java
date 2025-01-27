package com.sourcery.gymapp.backend.workout.service;

import com.sourcery.gymapp.backend.workout.mapper.RoutineMapper;
import com.sourcery.gymapp.backend.workout.repository.WorkoutRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class WorkoutStatsServiceTest {

    @Mock
    WorkoutCurrentUserService workoutCurrentUserService;

    @Mock
    OffsetDateService offsetDateService;

    @Mock
    WorkoutService workoutService;

    @Mock
    RoutineService routineService;

    @Mock
    WorkoutRepository workoutRepository;

    @Mock
    RoutineMapper routineMapper;

    @InjectMocks
    WorkoutStatsService workoutStatsService;

    @BeforeEach
    void setUp() {

    }

    @Nested
    @DisplayName("Get Workout Stats Tests")
    public class GetWorkoutStatsTests {

        @Test
        void shouldGetWorkoutStatsSuccessfully() {
        }
    }
    @Test
    void getWorkoutStats() {
    }

    @Test
    void getMostUsedRoutines() {
    }

    @Test
    void getTotalMuscleSets() {
    }

    @Test
    void checkIfUserIsNew() {
    }
}