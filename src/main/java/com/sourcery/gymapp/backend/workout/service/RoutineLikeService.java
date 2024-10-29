package com.sourcery.gymapp.backend.workout.service;

import com.sourcery.gymapp.backend.workout.dto.ResponseRoutineLikeDto;
import com.sourcery.gymapp.backend.workout.exception.RoutineNotFoundException;
import com.sourcery.gymapp.backend.workout.mapper.RoutineLikeMapper;
import com.sourcery.gymapp.backend.workout.model.Routine;
import com.sourcery.gymapp.backend.workout.model.RoutineLike;
import com.sourcery.gymapp.backend.workout.repository.RoutineLikeRepository;
import com.sourcery.gymapp.backend.workout.repository.RoutineRepository;
import com.sourcery.gymapp.backend.workout.exception.UserRoutineLikeNotFound;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoutineLikeService {

    private final RoutineRepository routineRepository;
    private final RoutineLikeRepository routineLikeRepository;

    private final RoutineLikeMapper routineLikeMapper;
    private final WorkoutCurrentUserService currentUserService;

    public ResponseRoutineLikeDto createRoutineLike(UUID routineId) {

        UUID currentUserId = currentUserService.getCurrentUserId();
        System.out.println("currentUserId: %s".formatted(currentUserId));
//        Routine routine = routineRepository.findById(routineId).orElseThrow(()-> new RoutineNotFoundException(routineId));

        RoutineLike routineLike = routineLikeMapper.createEntity(currentUserId, routineId);

        routineLikeRepository.save(routineLike);

        return routineLikeMapper.toDto(routineLike);
    }

    public ResponseRoutineLikeDto deleteRoutineLike(UUID routineId) {

        UUID currentUserId = currentUserService.getCurrentUserId();

        RoutineLike routineLike = routineLikeRepository.findByRoutineIdAndUserId(routineId, currentUserId)
                .orElseThrow(() -> new UserRoutineLikeNotFound(routineId));

        routineLikeRepository.delete(routineLike);

        return routineLikeMapper.toDto(routineLike);
    }
}
