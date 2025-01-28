package com.sourcery.gymapp.backend.workout.service;

import com.sourcery.gymapp.backend.workout.dto.ResponseRoutineSimpleDto;
import com.sourcery.gymapp.backend.workout.dto.WorkoutStatsDto;
import com.sourcery.gymapp.backend.workout.factory.RoutineFactory;
import com.sourcery.gymapp.backend.workout.mapper.RoutineMapper;
import com.sourcery.gymapp.backend.workout.model.Routine;
import com.sourcery.gymapp.backend.workout.repository.WorkoutRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WorkoutStatsServiceTest {

    @Mock
    Clock fixedClock;

    @Mock
    WorkoutCurrentUserService workoutCurrentUserService;

    @Mock
    OffsetDateService offsetDateService;

    @Mock
    WorkoutRepository workoutRepository;

    @Mock
    RoutineMapper routineMapper;

    @InjectMocks
    WorkoutStatsService workoutStatsService;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.fromString("910cb97b-d601-4c02-b4b6-c9f985e51a1f");
        when(workoutCurrentUserService.getCurrentUserId()).thenReturn(userId);

        fixedClock = Clock.fixed(Instant.parse("2023-01-15T12:00:00Z"), ZoneId.of("UTC"));
    }

    @Nested
    @DisplayName("Get Workout Stats Tests")
    public class GetWorkoutStatsTests {
        List<ZonedDateTime> currentMonthRange;
        List<ZonedDateTime> previousMonthRange;

        @BeforeEach
        void setUp() {
            currentMonthRange = List.of(
                    ZonedDateTime.parse("2023-01-01T00:00:00Z"),
                    ZonedDateTime.parse("2023-01-31T23:59:59Z")
            );
            previousMonthRange = List.of(
                    ZonedDateTime.parse("2022-12-01T00:00:00Z"),
                    ZonedDateTime.parse("2022-12-31T23:59:59Z")
            );
        }

        @Test
        void shouldGetWorkoutStatsInFiveMessages_WhenCurrentMonthStatsBetterThanPrevious() {
            when(offsetDateService.getMonthlyDateRangeOffset(0)).thenReturn(currentMonthRange);
            when(offsetDateService.getMonthlyDateRangeOffset(1)).thenReturn(previousMonthRange);

            when(workoutRepository.countWorkoutsByUserIdAndDateBetween(userId, currentMonthRange.get(0), currentMonthRange.get(1)))
                    .thenReturn(10);
            when(workoutRepository.countWorkoutsByUserIdAndDateBetween(userId, previousMonthRange.get(0), previousMonthRange.get(1)))
                    .thenReturn(5);
            when(workoutRepository.getTotalWeightByUserIdAndDateBetween(userId, currentMonthRange.get(0), currentMonthRange.get(1)))
                    .thenReturn(100000);
            when(workoutRepository.getTotalWeightByUserIdAndDateBetween(userId, previousMonthRange.get(0), previousMonthRange.get(1)))
                    .thenReturn(50000);

            List<WorkoutStatsDto> workoutStats = workoutStatsService.getWorkoutStats();

            List<String> expectedMessages = List.of(
                    "You have completed 10 workouts this month!",
                    "You have completed 5 more workouts than the last month!",
                    "You have lifted a total of 100000 kg this month!",
                    "100000 kg is like a fully loaded Boeing 747! ✈️",
                    "You have lifted 50000 kg more than the last month!"
            );

            assertEquals(5, workoutStats.size());
            assertAll("Checking all expected messages are present",
                    expectedMessages.stream().map(expectedMessage ->
                            () -> assertTrue(workoutStats.stream()
                                            .anyMatch(dto -> dto.content().equals(expectedMessage)),
                                    "Expected message should be present: " + expectedMessage))
            );
        }

        @Test
        void shouldGetWorkoutStatsInFiveMessagesWithoutPlural_WhenCurrentMonthStatsBetterThanPrevious() {
            when(offsetDateService.getMonthlyDateRangeOffset(0)).thenReturn(currentMonthRange);
            when(offsetDateService.getMonthlyDateRangeOffset(1)).thenReturn(previousMonthRange);

            when(workoutRepository.countWorkoutsByUserIdAndDateBetween(userId, currentMonthRange.get(0), currentMonthRange.get(1)))
                    .thenReturn(1);
            when(workoutRepository.countWorkoutsByUserIdAndDateBetween(userId, previousMonthRange.get(0), previousMonthRange.get(1)))
                    .thenReturn(0);
            when(workoutRepository.getTotalWeightByUserIdAndDateBetween(userId, currentMonthRange.get(0), currentMonthRange.get(1)))
                    .thenReturn(10000);
            when(workoutRepository.getTotalWeightByUserIdAndDateBetween(userId, previousMonthRange.get(0), previousMonthRange.get(1)))
                    .thenReturn(0);

            List<WorkoutStatsDto> workoutStats = workoutStatsService.getWorkoutStats();

            List<String> expectedMessages = List.of(
                    "You have completed 1 workout this month!",
                    "You have completed 1 more workout than the last month!",
                    "You have lifted a total of 10000 kg this month!",
                    "10000 kg is like lifting a double-decker bus! 🚌",
                    "You have lifted 10000 kg more than the last month!"
            );

            assertEquals(5, workoutStats.size());
            assertAll("Checking all expected messages are present",
                    expectedMessages.stream().map(expectedMessage ->
                            () -> assertTrue(workoutStats.stream()
                                            .anyMatch(dto -> dto.content().equals(expectedMessage)),
                                    "Expected message should be present: " + expectedMessage))
            );
        }

        @Test
        void shouldGetWorkoutStatsInThreeMessages_WhenPreviousMonthStatsBetter() {
            when(offsetDateService.getMonthlyDateRangeOffset(0)).thenReturn(currentMonthRange);
            when(offsetDateService.getMonthlyDateRangeOffset(1)).thenReturn(previousMonthRange);

            when(workoutRepository.countWorkoutsByUserIdAndDateBetween(userId, currentMonthRange.get(0), currentMonthRange.get(1)))
                    .thenReturn(3);
            when(workoutRepository.countWorkoutsByUserIdAndDateBetween(userId, previousMonthRange.get(0), previousMonthRange.get(1)))
                    .thenReturn(5);
            when(workoutRepository.getTotalWeightByUserIdAndDateBetween(userId, currentMonthRange.get(0), currentMonthRange.get(1)))
                    .thenReturn(50000);
            when(workoutRepository.getTotalWeightByUserIdAndDateBetween(userId, previousMonthRange.get(0), previousMonthRange.get(1)))
                    .thenReturn(100000);

            List<WorkoutStatsDto> workoutStats = workoutStatsService.getWorkoutStats();

            List<String> expectedMessages = List.of(
                    "You have completed 3 workouts this month!",
                    "You have lifted a total of 50000 kg this month!",
                    "50000 kg is the weight of a space shuttle ready for launch! 🚀"
            );

            assertEquals(3, workoutStats.size());
            assertAll("Checking all expected messages are present",
                    expectedMessages.stream().map(expectedMessage ->
                            () -> assertTrue(workoutStats.stream()
                                            .anyMatch(dto -> dto.content().equals(expectedMessage)),
                                    "Expected message should be present: " + expectedMessage))
            );
        }

        @Test
        void shouldGetEmptyWorkoutStats_whenNoWorkoutsCurrentMonth() {
            when(offsetDateService.getMonthlyDateRangeOffset(0)).thenReturn(currentMonthRange);
            when(offsetDateService.getMonthlyDateRangeOffset(1)).thenReturn(previousMonthRange);

            when(workoutRepository.countWorkoutsByUserIdAndDateBetween(userId, currentMonthRange.get(0), currentMonthRange.get(1)))
                    .thenReturn(0);
            when(workoutRepository.countWorkoutsByUserIdAndDateBetween(userId, previousMonthRange.get(0), previousMonthRange.get(1)))
                    .thenReturn(12);
            when(workoutRepository.getTotalWeightByUserIdAndDateBetween(userId, currentMonthRange.get(0), currentMonthRange.get(1)))
                    .thenReturn(0);
            when(workoutRepository.getTotalWeightByUserIdAndDateBetween(userId, previousMonthRange.get(0), previousMonthRange.get(1)))
                    .thenReturn(70000);

            List<WorkoutStatsDto> workoutStats = workoutStatsService.getWorkoutStats();

            assertEquals(0, workoutStats.size());
        }
    }

    @Nested
    @DisplayName("Get Most Used Routines Tests")
    public class getMostUsedRoutinesTests {

        @Test
        void shouldGetMostUsedRoutines() {
            List<Routine> routines = new ArrayList<>();

            for (int i = 0; i < 10; i++) {
                routines.add(RoutineFactory.createRoutine());
            }

            List<ZonedDateTime> offsetStartMonthRange = List.of(
                    ZonedDateTime.parse("2023-10-01T00:00:00Z"),
                    ZonedDateTime.parse("2023-01-31T23:59:59Z")
            );

            when(offsetDateService.getStartOffsetAndEndCurrentMonth(3)).thenReturn(offsetStartMonthRange);

            when(workoutRepository.getMostUsedRoutinesByUserIdAndDateBetween(userId, offsetStartMonthRange.get(0), offsetStartMonthRange.get(1)))
                    .thenReturn(routines);

            List<ResponseRoutineSimpleDto> mostUsedRoutines = workoutStatsService.getMostUsedRoutines(7, 3);

            assertEquals(7, mostUsedRoutines.size());
        }

    }

    @Test
    void getTotalMuscleSets() {
    }

    @Test
    void checkIfUserIsNew() {
    }
}