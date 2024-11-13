package com.sourcery.gymapp.backend.workout.service;

import com.sourcery.gymapp.backend.workout.dto.CreateRoutineDto;
import com.sourcery.gymapp.backend.workout.dto.ResponseRoutineDto;
import com.sourcery.gymapp.backend.workout.dto.RoutinePageDto;
import com.sourcery.gymapp.backend.workout.exception.RoutineNotFoundException;
import com.sourcery.gymapp.backend.workout.exception.UserNotAuthorizedException;
import com.sourcery.gymapp.backend.workout.exception.UserNotFoundException;
import com.sourcery.gymapp.backend.workout.mapper.RoutineMapper;
import com.sourcery.gymapp.backend.workout.model.Routine;
import com.sourcery.gymapp.backend.workout.repository.RoutineRepository;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoutineService {
    private final RoutineRepository routineRepository;
    private final RoutineMapper routineMapper;
    private final WorkoutCurrentUserService currentUserService;
    private final RoutineLikeService routineLikeService;

    @Transactional
    public ResponseRoutineDto createRoutine(CreateRoutineDto routineDto) {
        UUID currentUserId = currentUserService.getCurrentUserId();
        if (currentUserId == null) {
            throw new UserNotFoundException();
        }
        Routine routine = routineMapper.toEntity(routineDto, currentUserId);
        routineRepository.save(routine);
        long likesCount = routineLikeService.getRoutineLikes(routine.getId()).likeCount();
        return routineMapper.toDto(routine, likesCount);
    }

    public ResponseRoutineDto getRoutineById(UUID routineId) {
        Routine routine = findRoutineById(routineId);
        long likesCount = routineLikeService.getRoutineLikes(routineId).likeCount();
        return routineMapper.toDto(routine, likesCount);
    }

    public List<ResponseRoutineDto> getRoutinesByUserId() {
        UUID currentUserId = currentUserService.getCurrentUserId();
        List<Routine> routines = routineRepository.findByUserId(currentUserId);

        Map<UUID, Long> likesCounts = getLikeCountsMapForRoutines(routines);

        return getResponseRoutineDtoList(routines, likesCounts);
    }

    public RoutinePageDto searchRoutines(String name, Pageable pageable) {
        Page<Routine> routinePage = (name == null || name.isBlank())
                ? getAllRoutines(pageable)
                : routineRepository.findByNameIgnoreCaseContaining(name, pageable);

        List<Routine> routines = routinePage.getContent();

        Map<UUID, Long> likesCounts = getLikeCountsMapForRoutines(routines);

        List<ResponseRoutineDto> routineDtos = getResponseRoutineDtoList(routines, likesCounts);

        return new RoutinePageDto(routinePage.getTotalPages(), routinePage.getTotalElements(), routineDtos);
    }

    private List<ResponseRoutineDto> getResponseRoutineDtoList(List<Routine> routines,
                                                Map<UUID, Long> likesCounts) {
        return routines.stream()
                .map(routine -> routineMapper.toDto(routine,
                        likesCounts.getOrDefault(routine.getId(), 0L)))
                .collect(Collectors.toList());
    }

    private Map<UUID, Long> getLikeCountsMapForRoutines(List<Routine> routines) {
        return routineLikeService.getLikesCountsForRoutines(
                routines.stream().map(Routine::getId).collect(Collectors.toList())
        );
    }

    @Transactional
    public ResponseRoutineDto updateRoutine(UUID routineId, CreateRoutineDto routineDto) {
        UUID currentUserId = currentUserService.getCurrentUserId();
        Routine routine = findRoutineById(routineId);

        checkIsUserAuthorized(currentUserId, routine.getUserId());

        routineMapper.updateEntity(routine, routineDto);

        routineRepository.save(routine);

        long likesCount = routineLikeService.getRoutineLikes(routineId).likeCount();
        return routineMapper.toDto(routine, likesCount);
    }

    @Transactional
    public void deleteRoutine(UUID id) {
        UUID currentUserId = currentUserService.getCurrentUserId();
        Routine routine = findRoutineById(id);

        checkIsUserAuthorized(currentUserId, routine.getUserId());

        routineRepository.delete(routine);
    }

    public Routine findRoutineById(UUID id) {

        return routineRepository.findById(id).orElseThrow(() -> new RoutineNotFoundException(id));
    }

    private Page<Routine> getAllRoutines(Pageable pageable) {
        return routineRepository.findAll(pageable);
    }

    private void checkIsUserAuthorized(UUID currentUserId, UUID routineUserId) {

        if (!routineUserId.equals(currentUserId)) {
            throw new UserNotAuthorizedException();
        }
    }
}
