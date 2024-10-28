package com.sourcery.gymapp.backend.workout.mapper;

import com.sourcery.gymapp.backend.workout.dto.ResponseRoutineLikeDto;
import com.sourcery.gymapp.backend.workout.model.Routine;
import com.sourcery.gymapp.backend.workout.model.RoutineLike;
import org.springframework.stereotype.Component;
import java.util.UUID;
@Component
public class RoutineLikeMapper {

    public RoutineLike createEntity(UUID userId, Routine routine){
        RoutineLike routineLike = new RoutineLike();

        routineLike.setUserId(userId);
        routineLike.setRoutine(routine);

        return routineLike;
    }

    public ResponseRoutineLikeDto toDto(RoutineLike routineLike) {
        return new ResponseRoutineLikeDto(
                routineLike.getRoutine().getId(),
                routineLike.getUserId()
                );
    }
}
