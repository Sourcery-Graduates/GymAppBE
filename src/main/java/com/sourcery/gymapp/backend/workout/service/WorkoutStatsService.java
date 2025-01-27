package com.sourcery.gymapp.backend.workout.service;

import com.sourcery.gymapp.backend.workout.dto.*;
import com.sourcery.gymapp.backend.workout.mapper.RoutineMapper;
import com.sourcery.gymapp.backend.workout.model.Routine;
import com.sourcery.gymapp.backend.workout.repository.WorkoutRepository;
import com.sourcery.gymapp.backend.workout.util.WeightComparisonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class WorkoutStatsService {
    private final WorkoutCurrentUserService workoutCurrentUserService;
    private final OffsetDateService offsetDateService;
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

        List<WorkoutStatsDto> userStats = new ArrayList<>();

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

    private int getWorkoutCount(Integer offsetMonth) {
        UUID currentUserId = workoutCurrentUserService.getCurrentUserId();
        List<ZonedDateTime> startAndEndOfTheMonth = offsetDateService.getMonthlyDateRangeOffset(offsetMonth);

        return workoutRepository
                .countWorkoutsByUserIdAndDateBetween(
                        currentUserId,
                        startAndEndOfTheMonth.getFirst(),
                        startAndEndOfTheMonth.getLast()
                );
    }

    private int getTotalWeight(Integer offsetMonth) {
        UUID currentUserId = workoutCurrentUserService.getCurrentUserId();
        List<ZonedDateTime> startAndEndOfTheMonth = offsetDateService.getMonthlyDateRangeOffset(offsetMonth);

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

        List<ZonedDateTime> startAndEndOfTheMonth = offsetDateService.getStartOffsetAndEndCurrentMonth(offsetStartMonth);

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
        List<ZonedDateTime> startAndEndOfTheWeek = offsetDateService.getWeeklyDateRangeOffset(offsetWeek);

        return workoutRepository.getTotalMuscleSetsByUserIdAndDateBetween(
                currentUserId,
                startAndEndOfTheWeek.getFirst(),
                startAndEndOfTheWeek.getLast()
        );
    }

    public boolean checkIfUserIsNew() {
        List<ResponseWorkoutDto> workouts = workoutService.getWorkoutsByUserId();
        List<ResponseRoutineDto> routines = routineService.getRoutinesByUserId();

        return workouts.isEmpty() && routines.isEmpty();
    }
}
