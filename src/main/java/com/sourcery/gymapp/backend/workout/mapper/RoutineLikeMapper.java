package com.sourcery.gymapp.backend.workout.mapper;

import com.sourcery.gymapp.backend.workout.dto.ResponseRoutineLikeDto;
import com.sourcery.gymapp.backend.workout.model.Routine;
import com.sourcery.gymapp.backend.workout.model.RoutineLike;
import com.sourcery.gymapp.backend.workout.model.RoutineLikeId;
import org.springframework.stereotype.Component;
import java.util.UUID;
@Component
public class RoutineLikeMapper {

    public RoutineLike createEntity(UUID userId, UUID routineId){
        RoutineLike routineLike = new RoutineLike();

        routineLike.setId(new RoutineLikeId(userId, routineId));
        routineLike.setUserId(userId);
        routineLike.setRoutineId(routineId);

        return routineLike;
    }

    public ResponseRoutineLikeDto toDto(RoutineLike routineLike) {
        return new ResponseRoutineLikeDto(
                routineLike.getRoutineId(),
                routineLike.getUserId()
                );
    }
}
