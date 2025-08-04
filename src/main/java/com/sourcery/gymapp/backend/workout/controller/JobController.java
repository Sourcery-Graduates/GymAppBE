package com.sourcery.gymapp.backend.workout.controller;

import com.sourcery.gymapp.backend.workout.batch.job.WorkoutJobLauncher;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workout/job")
public class JobController {
    private final WorkoutJobLauncher workoutJobLauncher;

    @PostMapping("/last-workout-reminder")
    public void launchLastWorkoutReminderJob() {
        workoutJobLauncher.launchLastWorkoutReminderJobManually();
    }
}
