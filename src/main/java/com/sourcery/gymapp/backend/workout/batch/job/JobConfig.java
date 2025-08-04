package com.sourcery.gymapp.backend.workout.batch.job;

import com.sourcery.gymapp.backend.events.LastUserWorkoutEvent;
import com.sourcery.gymapp.backend.workout.batch.dto.LastUserWorkoutDto;
import com.sourcery.gymapp.backend.workout.batch.processor.LastWorkoutReminderProcessor;
import com.sourcery.gymapp.backend.workout.batch.reader.WorkoutReader;
import com.sourcery.gymapp.backend.workout.batch.writer.WorkoutWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JobConfig {
    private final JobRepository jobRepository;
    private final DataSourceTransactionManager dataSourceTransactionManager;
    private final WorkoutReader workoutReader;
    private final LastWorkoutReminderProcessor lastWorkoutReminderProcessor;
    private final WorkoutWriter workoutWriter;

    @Bean(name = "lastWorkoutReminderJob")
    public Job lastWorkoutReminderJob() {
        return new JobBuilder("lastWorkoutReminderJob", jobRepository)
                .start(lastWorkoutReminderChunkStep(dataSourceTransactionManager))
                .build();
    }

    public Step lastWorkoutReminderChunkStep(DataSourceTransactionManager transactionManager) {
        return new StepBuilder("lastWorkoutReminderChunkStep", jobRepository)
                .<LastUserWorkoutDto, LastUserWorkoutEvent>chunk(3, transactionManager)
                .reader(workoutReader.lastWorkoutReminderReader())
                .processor(lastWorkoutReminderProcessor)
                .writer(workoutWriter.lastWorkoutReminderWriter())
                .build();
    }
}
