package com.sourcery.gymapp.backend.workout.controller;

import com.sourcery.gymapp.backend.workout.dto.CreateRoutineDto;
import com.sourcery.gymapp.backend.workout.dto.ResponseRoutineDto;
import com.sourcery.gymapp.backend.workout.dto.ResponseRoutineLikeDto;
import com.sourcery.gymapp.backend.workout.service.RoutineLikeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workout/routine-like/")
public class RoutineLikeController {

    private final RoutineLikeService routineLikeService;

    @PostMapping("/{id}")
    public ResponseRoutineLikeDto createRoutineLike(
            @PathVariable("id") UUID routineId) {

        return routineLikeService.createRoutineLike(routineId);
    }

    @DeleteMapping("/{id}")
    public ResponseRoutineLikeDto deleteRoutineLike(
            @PathVariable("id") UUID routineId) {

        return routineLikeService.deleteRoutineLike(routineId);
    }

//    @GetMapping("/{id}/likes")
//    public ResponseRoutineLikeDto getRoutineLikesCount(
//            @PathVariable("id") UUID routineId){
//
//        return routineLikeService.getRoutineLikesCount(routineId);
//    }
//
//    @GetMapping("/{id}")
//    public ResponseRoutineLikeDto getUserRoutineLike(@PathVariable("id") UUID routineId){
//
//        return routineLikeService.getUserRoutineLike(routineId);
//    }
}
