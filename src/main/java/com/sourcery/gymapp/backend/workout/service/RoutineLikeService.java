package com.sourcery.gymapp.backend.workout.service;

import com.sourcery.gymapp.backend.workout.dto.ResponseLikeCountDto;
import com.sourcery.gymapp.backend.workout.exception.UserNotFoundException;
import com.sourcery.gymapp.backend.workout.model.RoutineLike;
import com.sourcery.gymapp.backend.workout.repository.RoutineLikeRepository;
import com.sourcery.gymapp.backend.workout.repository.projection.RoutineLikeCountProjection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoutineLikeService {

    private final RoutineLikeRepository routineLikeRepository;
    private final WorkoutCurrentUserService currentUserService;

    private UUID getCurrentUserIdOrThrow() {
        UUID currentUserId = currentUserService.getCurrentUserId();
        if (currentUserId == null) {
            throw new UserNotFoundException("Current user not found");
        }
        return currentUserId;
    }

    @Transactional
    public void addLikeToRoutine(UUID routineId) {
        UUID currentUserId = getCurrentUserIdOrThrow();
        if (!routineLikeRepository.existsByRoutineIdAndUserId(routineId, currentUserId)) {
            RoutineLike newLikeRecord = createNewLikeRecord(routineId, currentUserId);
            routineLikeRepository.save(newLikeRecord);
        }
    }

    private RoutineLike createNewLikeRecord(UUID routineId, UUID userId) {
        RoutineLike newLikeRecord = new RoutineLike();
        newLikeRecord.setRoutineId(routineId);
        newLikeRecord.setUserId(userId);
        return newLikeRecord;
    }

    @Transactional
    public void removeLikeFromRoutine(UUID routineId) {
        UUID currentUserId = getCurrentUserIdOrThrow();
        if (routineLikeRepository.existsByRoutineIdAndUserId(routineId, currentUserId)) {
            routineLikeRepository.deleteByRoutineIdAndUserId(routineId, currentUserId);
        }
    }

    @Transactional(readOnly = true)
    public ResponseLikeCountDto getRoutineLikes(UUID routineId) {
        long likeCount = routineLikeRepository.countByRoutineId(routineId);
        return new ResponseLikeCountDto(routineId, likeCount);
    }

    @Transactional(readOnly = true)
    public Map<UUID, Long> getLikesCountsForRoutines(List<UUID> routineIds) {
        return routineLikeRepository.findLikesCountsByRoutineIds(routineIds)
                .stream()
                .collect(Collectors.toMap(
                        RoutineLikeCountProjection::getRoutineId,
                        RoutineLikeCountProjection::getLikeCount
                ));
    }
}
