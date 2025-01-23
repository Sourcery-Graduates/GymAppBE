package com.sourcery.gymapp.backend.workout.service;

import com.sourcery.gymapp.backend.workout.dto.*;
import com.sourcery.gymapp.backend.workout.mapper.RoutineMapper;
import com.sourcery.gymapp.backend.workout.model.Routine;
import com.sourcery.gymapp.backend.workout.repository.WorkoutRepository;
import com.sourcery.gymapp.backend.workout.util.WeightComparisonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
@RequiredArgsConstructor
public class WorkoutStatsService {
    private final WorkoutCurrentUserService workoutCurrentUserService;
    private final WorkoutService workoutService;
    private final RoutineService routineService;
    private final WorkoutRepository workoutRepository;
    private final RoutineMapper routineMapper;

    public List<WorkoutStatsDto> getWorkoutStats() {
        int totalWorkoutsCurrentMonth = getWorkoutCount(0);
        int totalWorkoutsPreviousMonth = getWorkoutCount(1);
        int totalWeightCurrentMonth = getTotalWeight(0);
        int totalWeightPreviousMonth = getTotalWeight(1);
        int differenceInWorkouts = totalWorkoutsCurrentMonth - totalWorkoutsPreviousMonth;
        int differenceInWeight = totalWeightCurrentMonth - totalWeightPreviousMonth;
        boolean isNewUser = checkIfUserIsNew();

        List<WorkoutStatsDto> userStats = new ArrayList<>();

        if (isNewUser) {
            userStats.add(new WorkoutStatsDto(
                    UUID.randomUUID(),
                    "user",
                    "newUser"
            ));

            return userStats;
        }

        if (totalWorkoutsCurrentMonth >= 1) {
            String isPlural = totalWorkoutsCurrentMonth > 1 ? "s" : "";
            userStats.add(new WorkoutStatsDto(
                    UUID.randomUUID(),
                    "totalWorkouts",
                    "You have completed %d workout%s this month!"
                            .formatted(totalWorkoutsCurrentMonth, isPlural)
            ));
        }

        if (differenceInWorkouts >= 1) {
            String isPlural = totalWorkoutsCurrentMonth > 1 ? "s" : "";
            userStats.add(new WorkoutStatsDto(
                    UUID.randomUUID(),
                    "workoutDifference",
                    "You have completed %d more workout%s than the last month!"
                            .formatted(differenceInWorkouts, isPlural)
            ));
        }

        if (totalWeightCurrentMonth > 0) {
            userStats.add(new WorkoutStatsDto(
                    UUID.randomUUID(),
                    "totalWeight",
                    "You have lifted a total of " + totalWeightCurrentMonth + " kg this month!"
            ));

            userStats.add(new WorkoutStatsDto(
                    UUID.randomUUID(),
                    "totalWeight",
                    totalWeightCurrentMonth +" kg " + WeightComparisonUtil.getMessageByWeight(totalWeightCurrentMonth)
            ));
        }

        if (differenceInWeight > 0) {
            userStats.add(new WorkoutStatsDto(
                    UUID.randomUUID(),
                    "weightDifference",
                    "You have lifted " + differenceInWeight + " kg more than the last month!"
            ));
        }
        return userStats;
    }

    public int getWorkoutCount(Integer offsetMonth) {
        UUID currentUserId = workoutCurrentUserService.getCurrentUserId();
        List<ZonedDateTime> startAndEndOfTheMonth = getStartAndEndOffsetMonth(offsetMonth);

        return workoutRepository
                .countWorkoutsByUserIdAndDateBetween(
                        currentUserId,
                        startAndEndOfTheMonth.getFirst(),
                        startAndEndOfTheMonth.getLast()
                );
    }

    public int getTotalWeight(Integer offsetMonth) {
        UUID currentUserId = workoutCurrentUserService.getCurrentUserId();
        List<ZonedDateTime> startAndEndOfTheMonth = getStartAndEndOffsetMonth(offsetMonth);

        Integer totalWeight = workoutRepository.getTotalWeightByUserIdAndDateBetween(
                currentUserId,
                startAndEndOfTheMonth.getFirst(),
                startAndEndOfTheMonth.getLast()
        );

        return totalWeight != null ? totalWeight : 0;
    }

    public List<ResponseRoutineSimpleDto> getMostUsedRoutines(Integer routinesLimit, Integer offsetStartMonth) {
        int baseRoutinesLimit = 7;
        if (routinesLimit != null) {
            baseRoutinesLimit = routinesLimit;
        }
        UUID currentUserId = workoutCurrentUserService.getCurrentUserId();

        List<ZonedDateTime> startAndEndOfTheMonth = getStartOffsetAndEndMonth(offsetStartMonth);

        List<Routine> routines = workoutRepository.getMostUsedRoutinesByUserIdAndDateBetween(
                currentUserId,
                startAndEndOfTheMonth.getFirst(),
                startAndEndOfTheMonth.getLast()
        );

        return routines.stream()
                .map(routineMapper::toSimpleDto)
                .limit(baseRoutinesLimit)
                .toList();
    }

    public List<MuscleSetDto> getTotalMuscleSets(Integer offsetWeek) {
        UUID currentUserId = workoutCurrentUserService.getCurrentUserId();
        List<ZonedDateTime> startAndEndOfTheWeek = getWeeklyDateRange(offsetWeek);

        return workoutRepository.getTotalMuscleSetsByUserIdAndDateBetween(
                currentUserId,
                startAndEndOfTheWeek.getFirst(),
                startAndEndOfTheWeek.getLast()
        );
    }

    public List<ZonedDateTime> getWeeklyDateRange(Integer offsetWeek) {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime startOfWeek = now.minusWeeks(offsetWeek)
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .withHour(0).withMinute(0).withSecond(0).withNano(0);

        ZonedDateTime endOfWeek = startOfWeek.plusDays(6)
                .withHour(23).withMinute(59).withSecond(59).withNano(999999999);

        return List.of(startOfWeek, endOfWeek);
    }


    private List<ZonedDateTime> getStartOffsetAndEndMonth(Integer offsetStartMonth) {
        ZonedDateTime currentDate = ZonedDateTime.now();

        ZonedDateTime startOfTheMonth = getOffsetStartMonthFromCurrentDate(offsetStartMonth, currentDate);
        ZonedDateTime endOfTheMonth = getEndMonthFromCurrentDate(currentDate);

        return List.of(startOfTheMonth, endOfTheMonth);
    }

    private List<ZonedDateTime> getStartAndEndOffsetMonth(Integer offsetMonth) {
        ZonedDateTime currentDate = ZonedDateTime.now();
        ZonedDateTime startOfTheMonth = currentDate.withDayOfMonth(1).withHour(0);
        ZonedDateTime endOfTheMonth = currentDate;

        if (offsetMonth != null) {
            startOfTheMonth = getOffsetStartMonthFromCurrentDate(offsetMonth, currentDate);

            endOfTheMonth = getOffsetEndMonthFromCurrentDate(offsetMonth, currentDate);
        }

        return List.of(startOfTheMonth, endOfTheMonth);
    }

    private static ZonedDateTime getOffsetStartMonthFromCurrentDate(Integer offsetStartMonth,
                                                                         ZonedDateTime currentDate) {
        return currentDate.minusMonths(offsetStartMonth)
                .with(TemporalAdjusters.firstDayOfMonth())
                .withHour(0).withMinute(0).withSecond(0).withNano(0);
    }

    private static ZonedDateTime getOffsetEndMonthFromCurrentDate(Integer offsetEndMonth,
                                                                       ZonedDateTime currentDate) {
        return currentDate.minusMonths(offsetEndMonth)
                .with(TemporalAdjusters.lastDayOfMonth())
                .withHour(23).withMinute(59).withSecond(59).withNano(999999999);
    }

    private static ZonedDateTime getEndMonthFromCurrentDate(ZonedDateTime currentDate) {
        return currentDate
                .with(TemporalAdjusters.lastDayOfMonth())
                .withHour(23).withMinute(59).withSecond(59).withNano(999999999);
    }


    private boolean checkIfUserIsNew() {
        List<ResponseWorkoutDto> workouts = workoutService.getWorkoutsByUserId();
        List<ResponseRoutineDto> routines = routineService.getRoutinesByUserId();

        return workouts.isEmpty() && routines.isEmpty();
    }
}
