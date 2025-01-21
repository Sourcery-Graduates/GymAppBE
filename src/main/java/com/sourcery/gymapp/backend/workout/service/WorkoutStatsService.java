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
        if (totalWorkoutsCurrentMonth == 1) {
            userStats.add(new WorkoutStatsDto(
                    UUID.randomUUID(),
                    "totalWorkouts",
                    "You have completed " + totalWorkoutsCurrentMonth + " workout this month!"
            ));
        } else if (totalWeightCurrentMonth > 1){
            userStats.add(new WorkoutStatsDto(
                    UUID.randomUUID(),
                    "totalWorkouts",
                    "You have completed " + totalWorkoutsCurrentMonth + " workouts this month!"
            ));
        }

        if (differenceInWorkouts == 1) {
            userStats.add(new WorkoutStatsDto(
                    UUID.randomUUID(),
                    "workoutDifference",
                    "You have completed " + differenceInWorkouts + " more workout than the last month!"
            ));
        } else if (differenceInWorkouts > 1) {
            userStats.add(new WorkoutStatsDto(
                    UUID.randomUUID(),
                    "workoutDifference",
                    "You have completed " + differenceInWorkouts + " more workouts than the last month!"
            ));
        }

        if (totalWeightCurrentMonth > 0) {
            userStats.add(new WorkoutStatsDto(
                    UUID.randomUUID(),
                    "totalWeight",
                    "You have lifted a total of " + totalWeightCurrentMonth + " kg this month!"
            ));
        }

        if (totalWeightCurrentMonth > 0) {
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

    public int getWorkoutCount(Integer month) {
        UUID currentUserId = workoutCurrentUserService.getCurrentUserId();
        List<ZonedDateTime> startAndEndOfTheMonth = getStartAndEndOfTheMonthFromCurrentDateMinusMonth(month);

        return workoutRepository
                .countWorkoutsByUserIdAndDateBetween(
                        currentUserId,
                        startAndEndOfTheMonth.getFirst(),
                        startAndEndOfTheMonth.getLast()
                );
    }

    public int getTotalWeight(Integer month) {
        UUID currentUserId = workoutCurrentUserService.getCurrentUserId();
        List<ZonedDateTime> startAndEndOfTheMonth = getStartAndEndOfTheMonthFromCurrentDateMinusMonth(month);

        Integer totalWeight = workoutRepository.getTotalWeightByUserIdAndDateBetween(
                currentUserId,
                startAndEndOfTheMonth.getFirst(),
                startAndEndOfTheMonth.getLast()
        );

        return totalWeight != null ? totalWeight : 0;
    }

    public List<ResponseRoutineSimpleDto> getMostUsedRoutines() {
        UUID currentUserId = workoutCurrentUserService.getCurrentUserId();
        List<ZonedDateTime> startAndEndOfTheMonth = getEndOfTheMonthFromCurrentDateAndStartOfTheMonthMinus(3);

        List<Routine> routines = workoutRepository.getMostUsedRoutinesByUserIdAndDateBetween(
                currentUserId,
                startAndEndOfTheMonth.getFirst(),
                startAndEndOfTheMonth.getLast()
        );

        return routines.stream().map(routineMapper::toSimpleDto).toList();
    }

    public List<MuscleSetDto> getTotalMuscleSetsByUserIdAndDateBetween() {
        UUID currentUserId = workoutCurrentUserService.getCurrentUserId();
        List<ZonedDateTime> startAndEndOfTheWeek = getCurrentWeek();

        return workoutRepository.getTotalMuscleSetsByUserIdAndDateBetween(
                currentUserId,
                startAndEndOfTheWeek.getFirst(),
                startAndEndOfTheWeek.getLast()
        );
    }

    private List<ZonedDateTime> getCurrentWeek() {
        ZonedDateTime currentDate = ZonedDateTime.now();
        ZonedDateTime startOfTheWeek = currentDate
                .with(TemporalAdjusters.previous(DayOfWeek.MONDAY))
                .withHour(0).withMinute(0).withSecond(0).withNano(0);;

        ZonedDateTime endOfTheWeek = startOfTheWeek
                .plusWeeks(1)
                .withHour(23).withMinute(59).withSecond(59).withNano(999999999);

        return List.of(startOfTheWeek, endOfTheWeek);
    }

    private List<ZonedDateTime> getEndOfTheMonthFromCurrentDateAndStartOfTheMonthMinus(Integer month) {
        ZonedDateTime currentDate = ZonedDateTime.now();

        ZonedDateTime startOfTheMonth = currentDate.minusMonths(month)
                .with(TemporalAdjusters.firstDayOfMonth())
                .withHour(0).withMinute(0).withSecond(0).withNano(0);

        ZonedDateTime endOfTheMonth = currentDate
            .with(TemporalAdjusters.lastDayOfMonth())
            .withHour(23).withMinute(59).withSecond(59).withNano(999999999);

        return List.of(startOfTheMonth, endOfTheMonth);
    }

    private List<ZonedDateTime> getStartAndEndOfTheMonthFromCurrentDateMinusMonth(Integer month) {
        ZonedDateTime currentDate = ZonedDateTime.now();
        ZonedDateTime startOfTheMonth = currentDate.withDayOfMonth(1).withHour(0);
        ZonedDateTime endOfTheMonth = currentDate;

        if (month != null && month > 0) {
            startOfTheMonth = currentDate.minusMonths(month)
                    .with(TemporalAdjusters.firstDayOfMonth())
                    .withHour(0).withMinute(0).withSecond(0).withNano(0);

            endOfTheMonth = currentDate.minusMonths(month)
                    .with(TemporalAdjusters.lastDayOfMonth())
                    .withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        }

        return List.of(startOfTheMonth, endOfTheMonth);
    }

    private boolean checkIfUserIsNew() {
        List<ResponseWorkoutDto> workouts = workoutService.getWorkoutsByUserId();
        List<ResponseRoutineDto> routines = routineService.getRoutinesByUserId();

        return workouts.isEmpty() && routines.isEmpty();
    }
}
