package com.sourcery.gymapp.backend.workout.batch.config;

import com.sourcery.gymapp.backend.events.LastUserWorkoutEvent;
import com.sourcery.gymapp.backend.workout.batch.dto.LastUserWorkoutDto;
import com.sourcery.gymapp.backend.workout.batch.job.JobConfig;
import com.sourcery.gymapp.backend.workout.batch.processor.LastWorkoutReminderProcessor;
import com.sourcery.gymapp.backend.workout.batch.reader.WorkoutReader;
import com.sourcery.gymapp.backend.workout.batch.writer.WorkoutWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LastWorkoutReminderJobConfigTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private DataSourceTransactionManager dataSourceTransactionManager;

    @Mock
    private WorkoutReader workoutReader;

    @Mock
    private JdbcCursorItemReader<LastUserWorkoutDto> itemReader;

    @Mock
    private LastWorkoutReminderProcessor lastWorkoutReminderProcessor;

    @Mock
    private WorkoutWriter workoutWriter;

    @Mock
    private ItemWriter<LastUserWorkoutEvent> itemWriter;

    @InjectMocks
    private JobConfig jobConfig;

    @BeforeEach
    void setup() {
        when(workoutReader.lastWorkoutReminderReader()).thenReturn(itemReader);
        when(workoutWriter.lastWorkoutReminderWriter()).thenReturn(itemWriter);
    }

    @Test
    void shouldBuildJobCorrectly() {
        Job job = jobConfig.lastWorkoutReminderJob();

        assertNotNull(job);
        assertEquals("lastWorkoutReminderJob", job.getName());
    }

    @Test
    void shouldBuildStepCorrectly() {
        Step step = jobConfig.lastWorkoutReminderChunkStep(dataSourceTransactionManager);

        assertNotNull(step);
        assertEquals("lastWorkoutReminderChunkStep", step.getName());
    }
}
