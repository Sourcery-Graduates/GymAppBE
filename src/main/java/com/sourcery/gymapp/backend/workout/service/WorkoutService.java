package com.sourcery.gymapp.backend.workout.service;

import com.sourcery.gymapp.backend.workout.dto.*;
import com.sourcery.gymapp.backend.workout.validation.ValidateOrderNumbersInCreateWorkoutDto;
import com.sourcery.gymapp.backend.workout.exception.UserNotFoundException;
import com.sourcery.gymapp.backend.workout.exception.WorkoutNotFoundException;
import com.sourcery.gymapp.backend.workout.mapper.WorkoutMapper;
import com.sourcery.gymapp.backend.workout.model.Exercise;
import com.sourcery.gymapp.backend.workout.model.Routine;
import com.sourcery.gymapp.backend.workout.model.Workout;
import com.sourcery.gymapp.backend.workout.repository.WorkoutRepository;
import com.sourcery.gymapp.backend.workout.util.AuthorizationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.zip.ZipEntry;

@Service
@RequiredArgsConstructor
public class WorkoutService {
    private final WorkoutRepository workoutRepository;
    private final RoutineService routineService;
    private final WorkoutCurrentUserService currentUserService;
    private final ExerciseService exerciseService;
    private final WorkoutMapper workoutMapper;
    private final WorkoutExerciseService workoutExerciseService;

    @Transactional
    @ValidateOrderNumbersInCreateWorkoutDto
    public ResponseWorkoutDto createWorkout(CreateWorkoutDto createWorkoutDto) {
        UUID currentUserId = getCurrentUserId();

        Workout basedOnWorkout = null;
        if (createWorkoutDto.basedOnWorkoutId() != null) {
            basedOnWorkout = findWorkoutById(createWorkoutDto.basedOnWorkoutId());
        }
        Routine routine = null;
        if (createWorkoutDto.routineId() != null) {
            routine = routineService.findRoutineById(createWorkoutDto.routineId());
        }
        Map<UUID, Exercise> exerciseMap = new HashMap<>();
        if (createWorkoutDto.exercises() != null) {
            exerciseMap = exerciseService.getExerciseMapByIds(
                    createWorkoutDto.exercises()
                            .stream()
                            .map(CreateWorkoutExerciseDto::exerciseId)
                            .toList()
            );
        }

        var workout = workoutMapper.toEntity(createWorkoutDto, currentUserId, basedOnWorkout, routine, exerciseMap);
        workout = workoutRepository.save(workout);

        return workoutMapper.toDto(workout);
    }

    @Transactional
    @ValidateOrderNumbersInCreateWorkoutDto
    public ResponseWorkoutDto updateWorkout(CreateWorkoutDto updateWorkoutDto, UUID workoutId) {
        var workout = findWorkoutById(workoutId);
        UUID currentUserId = getCurrentUserId();

        AuthorizationUtil.checkIsUserAuthorized(currentUserId, workout.getUserId());

        updateWorkoutFields(updateWorkoutDto, workout);
        workoutExerciseService.updateWorkoutExercises(updateWorkoutDto, workout);

        workout = workoutRepository.save(workout);

        return workoutMapper.toDto(workout);
    }

    public ResponseWorkoutDto getWorkoutById(UUID workoutId) {
        Workout workout = findWorkoutById(workoutId);

        return workoutMapper.toDto(workout);
    }

    public List<ResponseWorkoutDto> getWorkoutsByUserId() {
        UUID currentUserId = getCurrentUserId();

        List<Workout> workouts = workoutRepository.findByUserId(currentUserId,
                Sort.by(Sort.Order.asc("date"), Sort.Order.asc("name")));

        return workouts.stream()
                .map(workoutMapper::toDto)
                .toList();
    }

    @Transactional
    public void deleteWorkout(UUID workoutId) {
        var workout = findWorkoutById(workoutId);
        UUID currentUserId = getCurrentUserId();

        AuthorizationUtil.checkIsUserAuthorized(currentUserId, workout.getUserId());

        workoutRepository.delete(workout);
    }

    public ResponseWorkoutGridGroupedByDate getWorkoutGridGroupByDate(ZonedDateTime startDate,
                                                                      ZonedDateTime endDate) {
        UUID currentUserId = getCurrentUserId();

        HashMap<String, List<ResponseWorkoutDto>> workoutMap = new HashMap<>();
        DateTimeFormatter dateWithoutTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        List<Workout> workouts = workoutRepository.findByUserIdAndDateBetween(
                currentUserId,
                startDate,
                endDate,
                Sort.by(
                        Sort.Order.asc("date"),
                        Sort.Order.asc("name")
                )
        );

        workouts.forEach(workout ->
                workoutMap.computeIfAbsent(
                                workout.getDate().format(dateWithoutTimeFormatter),
                                key -> new ArrayList<>()
                        )
                        .add(workoutMapper.toDto(workout))
        );
        return new ResponseWorkoutGridGroupedByDate(workoutMap);
    }

    public Workout findWorkoutById(UUID id) {

        return workoutRepository.findById(id)
                .orElseThrow(() -> new WorkoutNotFoundException(id));
    }

    private void updateWorkoutFields(
            CreateWorkoutDto dto,
            Workout workout) {

        workout.setName(dto.name());
        workout.setDate(dto.date());
        workout.setComment(dto.comment());
    }

    public int getWorkoutCount(Integer month) {
        UUID currentUserId = getCurrentUserId();
        List<ZonedDateTime> startAndEndOfTheMonth = getStartAndEndOfTheMonthFromCurrentDateMinusMonth(month);

        return workoutRepository
                .countWorkoutsByUserIdAndDateBetween(currentUserId,
                        startAndEndOfTheMonth.getFirst(),
                        startAndEndOfTheMonth.getLast());
    }

    public int getTotalWeight(Integer month) {
        UUID currentUserId = getCurrentUserId();
        List<ZonedDateTime> startAndEndOfTheMonth = getStartAndEndOfTheMonthFromCurrentDateMinusMonth(month);

        return workoutRepository.
                getTotalWeightByUserIdAndDateBetween(currentUserId,
                        startAndEndOfTheMonth.getFirst(),
                        startAndEndOfTheMonth.getLast());
    }

    private List<ZonedDateTime> getStartAndEndOfTheMonthFromCurrentDateMinusMonth(Integer month) {

        ZonedDateTime currentDate = ZonedDateTime.now();
        ZonedDateTime startOfTheMonth = currentDate.withDayOfMonth(1).withHour(0);
        ZonedDateTime endOfTheMonth = currentDate;

        if (month != null && month > 0) {
            startOfTheMonth = currentDate.minusMonths(month)
                    .with(TemporalAdjusters.firstDayOfMonth()).withHour(0).withMinute(0);

            endOfTheMonth = currentDate.minusMonths(month)
                    .with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59);
        }

        return List.of(startOfTheMonth, endOfTheMonth);
    }

    private UUID getCurrentUserId() {
        UUID currentUserId = currentUserService.getCurrentUserId();
        if (currentUserId == null) {
            throw new UserNotFoundException();
        }
        return currentUserId;
    }
}
