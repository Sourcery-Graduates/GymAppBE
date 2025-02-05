package com.sourcery.gymapp.backend.workout.service;

import com.sourcery.gymapp.backend.workout.dto.ExerciseDetailDto;
import com.sourcery.gymapp.backend.workout.dto.ExercisePageDto;
import com.sourcery.gymapp.backend.workout.exception.ExerciseNotFoundException;
import com.sourcery.gymapp.backend.workout.mapper.ExerciseMapper;
import com.sourcery.gymapp.backend.workout.model.Exercise;
import com.sourcery.gymapp.backend.workout.repository.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.HashSet;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExerciseService {
    private final ExerciseRepository exerciseRepository;
    private final ExerciseMapper exerciseMapper;

    public Map<UUID, Exercise> getExerciseMapByIds(List<UUID> exerciseIds) {
        List<Exercise> exercises = exerciseRepository.findAllByIdIn(exerciseIds);

        Map<UUID, Exercise> exerciseMap =
                exercises.stream().collect(Collectors.toMap(Exercise::getId, exercise -> exercise));

        Set<UUID> missingIds = new HashSet<>(exerciseIds);
        missingIds.removeAll(exerciseMap.keySet());

        if (!missingIds.isEmpty()) {
            throw new ExerciseNotFoundException(missingIds);
        }

        return exerciseMap;
    }

    public ExercisePageDto getFilteredExercises(String prefix, String primaryMuscle, Pageable pageable) {
        if (primaryMuscle == null || primaryMuscle.isBlank() && (prefix != null && !prefix.isBlank())) {
            return getExercisesByPrefix(prefix, pageable);
        } else if (!primaryMuscle.isBlank() && (prefix == null || prefix.isBlank())) {
            return getExercisesByPrimaryMuscle(primaryMuscle, pageable);
        } else {
            return getExercisesByPrimaryMuscleAndPrefix(prefix, primaryMuscle, pageable);
        }
    }

    public ExercisePageDto getExercisesByPrefix(String prefix, Pageable pageable) {
        Page<Exercise> exercisePage;

        if (prefix == null || prefix.isBlank()) {
            exercisePage = exerciseRepository.findAll(pageable);
        } else {
            exercisePage = exerciseRepository.findByPrefixOrContaining(prefix, pageable);
        }

        List<ExerciseDetailDto> exercises = exercisePage.map(exerciseMapper::toDto).getContent();

        return new ExercisePageDto(
                exercisePage.getTotalPages(),
                exercisePage.getTotalElements(),
                exercises
        );
    }

    public ExercisePageDto getExercisesByPrimaryMuscle(String primaryMuscle, Pageable pageable) {
        Page<Exercise> exercisePage;

        String primaryMusclePrepared = "{" + primaryMuscle + "}";

        exercisePage = exerciseRepository.findAllByPrimaryMuscle(primaryMusclePrepared, pageable);

        List<ExerciseDetailDto> exercises = exercisePage.map(exerciseMapper::toDto).getContent();

        return new ExercisePageDto(
                exercisePage.getTotalPages(),
                exercisePage.getTotalElements(),
                exercises
        );
    }

    public ExercisePageDto getExercisesByPrimaryMuscleAndPrefix(
            String prefix, String primaryMuscle, Pageable pageable) {
        String primaryMusclePrepared = "{" + primaryMuscle + "}";

        Page<Exercise> exercisePage = exerciseRepository.findAllByPrimaryMuscleAndPrefix(primaryMusclePrepared, prefix, pageable);

        List<ExerciseDetailDto> exercises = exercisePage.map(exerciseMapper::toDto).getContent();

        return new ExercisePageDto(
                exercisePage.getTotalPages(),
                exercisePage.getTotalElements(),
                exercises
        );
    }

    public Exercise findExerciseById(UUID id) {
        return exerciseRepository.findById(id)
                .orElseThrow(() -> new ExerciseNotFoundException(id));
    }
}
