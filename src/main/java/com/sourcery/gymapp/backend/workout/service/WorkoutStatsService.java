package com.sourcery.gymapp.backend.workout.service;

import com.sourcery.gymapp.backend.workout.dto.WorkoutStatsDto;
import com.sourcery.gymapp.backend.workout.repository.WorkoutRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkoutStatsService {
    private final WorkoutCurrentUserService workoutCurrentUserService;
    private final WorkoutRepository workoutRepository;

    public List<WorkoutStatsDto> getWorkoutStats() {
        int totalWorkoutsCurrentMonth = getWorkoutCount(0);
        int totalWorkoutsPreviousMonth = getWorkoutCount(1);
        int totalWeightCurrentMonth = getTotalWeight(0);
        int totalWeightPreviousMonth = getTotalWeight(1);
        int differenceInWorkouts = totalWorkoutsCurrentMonth - totalWorkoutsPreviousMonth;
        int differenceInWeight = totalWeightCurrentMonth - totalWeightPreviousMonth;

        List<WorkoutStatsDto> userStats = new ArrayList<>();

        if (totalWorkoutsCurrentMonth == 1) {
            userStats.add(new WorkoutStatsDto(
                    UUID.randomUUID(),
                    "workout",
                    "You have completed " + totalWorkoutsCurrentMonth + " workout this month!"
            ));
        } else {
            userStats.add(new WorkoutStatsDto(
                    UUID.randomUUID(),
                    "workout",
                    "You have completed " + differenceInWorkouts + " more workout than the last month!"
            ));
        }

        if (differenceInWorkouts > 1) {
            userStats.add(new WorkoutStatsDto(
                    UUID.randomUUID(),
                    "workout",
                    "You have completed " + differenceInWorkouts + " more workouts than the last month!"
            ));
        }
        if (totalWeightCurrentMonth > 0) {
            userStats.add(new WorkoutStatsDto(
                    UUID.randomUUID(),
                    "weight",
                    "You have lifted a total of " + totalWeightCurrentMonth + " kg this month!"
            ));
        }
        if (differenceInWeight != 0) {
            userStats.add(new WorkoutStatsDto(
                    UUID.randomUUID(),
                    "weight",
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
}
