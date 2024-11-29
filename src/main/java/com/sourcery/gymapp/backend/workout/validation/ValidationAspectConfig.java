package com.sourcery.gymapp.backend.workout.validation;

import com.sourcery.gymapp.backend.workout.dto.CreateWorkoutDto;
import com.sourcery.gymapp.backend.workout.dto.CreateWorkoutExerciseDto;
import com.sourcery.gymapp.backend.workout.dto.CreateWorkoutExerciseSetDto;
import com.sourcery.gymapp.backend.workout.exception.WrongOrderException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
public class ValidationAspectConfig {

    @Around("@annotation(ValidateOrderNumbers)")
    public Object validateOrder(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();

        for (Object arg : args) {
            if (arg instanceof CreateWorkoutDto && hasExercises((CreateWorkoutDto) arg)) {
                validateOrderInList(
                        ((CreateWorkoutDto) arg).exercises().stream()
                                .mapToInt(CreateWorkoutExerciseDto::orderNumber)
                                .boxed()
                                .sorted()
                                .toList()
                );

                for (CreateWorkoutExerciseDto exercise : ((CreateWorkoutDto) arg).exercises()) {
                    if (exercise.sets() != null && !exercise.sets().isEmpty()) {
                        validateOrderInList(
                                exercise.sets().stream()
                                        .mapToInt(CreateWorkoutExerciseSetDto::setNumber)
                                        .boxed()
                                        .sorted()
                                        .toList()
                        );
                    }
                }
            }
        }

        return joinPoint.proceed();
    }

    private boolean hasExercises(CreateWorkoutDto dto) {
        return dto.exercises() != null && !dto.exercises().isEmpty();
    }

    private void validateOrderInList(List<Integer> orderNumbers) {
        if (orderNumbers.getFirst() != 1) {
            throw new WrongOrderException();
        }
        for (int i = 0; i < orderNumbers.size()-1; i++) {
            if (orderNumbers.get(i)+1 != orderNumbers.get(i+1)) {
                throw new WrongOrderException();
            }
        }
    }
}
