package com.sourcery.gymapp.backend.workout.batch.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.UUID;

@Component
@Slf4j
public class WorkoutJobLauncher {
    private final JobLauncher jobLauncher;
    private final Job lastWorkoutRemindUserJob;

    public WorkoutJobLauncher(JobLauncher jobLauncher,
                              @Qualifier("lastWorkoutReminderJob") Job lastWorkoutRemindUserJob) {
        this.jobLauncher = jobLauncher;
        this.lastWorkoutRemindUserJob = lastWorkoutRemindUserJob;
    }

    @Scheduled(cron = "0 00 18 ? * SUN")
    public void launchLastWorkoutRemindUserJobScheduled() {
        JobParameters jobParameters = getLastWorkoutRemindJobParameters();

        try {
            jobLauncher.run(lastWorkoutRemindUserJob, jobParameters);
        } catch (Exception e) {
            log.error("Failed to run scheduled lastWorkoutReminderJob", e);
        }
    }

    public void launchLastWorkoutReminderJobManually() {
        JobParameters jobParameters = getLastWorkoutRemindJobParameters();

        try {
            jobLauncher.run(lastWorkoutRemindUserJob, jobParameters);
        } catch (Exception e) {
            log.error("Failed to manual run lastWorkoutReminderJob", e);
        }
    }

    private static JobParameters getLastWorkoutRemindJobParameters() {
        String currentTime = ZonedDateTime.now().toString();
        String jobId = UUID.randomUUID().toString();

        return new JobParametersBuilder()
                .addString("id", jobId)
                .addString("launchDateTime", currentTime)
                .toJobParameters();
    }
}
