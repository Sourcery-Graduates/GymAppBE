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

    RoutineRepository routineRepository;
    RoutineLikeRepository routineLikeRepository;

    RoutineLikeMapper routineLikeMapper;
    WorkoutCurrentUserService currentUserService;

    public ResponseRoutineLikeDto createRoutineLike(UUID routineId) {

        UUID currentUserId = currentUserService.getCurrentUserId();
        Routine routine = routineRepository.findById(routineId).orElseThrow(()-> new RoutineNotFoundException(routineId));

        RoutineLike routineLike = routineLikeMapper.createEntity(currentUserId, routine);

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
