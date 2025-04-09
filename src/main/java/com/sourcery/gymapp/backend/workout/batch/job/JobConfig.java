package com.sourcery.gymapp.backend.workout.batch.job;

import com.sourcery.gymapp.backend.events.LastUserWorkoutEvent;
import com.sourcery.gymapp.backend.workout.batch.dto.LastUserWorkoutDto;
import com.sourcery.gymapp.backend.workout.batch.mapper.LastUserWorkoutDtoRowMapper;
import com.sourcery.gymapp.backend.workout.batch.processor.LastUserWorkoutProcessor;
import com.sourcery.gymapp.backend.workout.producer.WorkoutKafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@RequiredArgsConstructor
public class JobConfig {
    private final JobRepository jobRepository;
    private final DataSource dataSource;
    private final DataSourceTransactionManager dataSourceTransactionManager;
    private final WorkoutKafkaProducer workoutKafkaProducer;
    private final LastUserWorkoutProcessor lastUserWorkoutProcessor;

    @Bean(name = "lastWorkoutReminderJob")
    public Job lastWorkoutReminderJob() {
        return new JobBuilder("lastWorkoutReminderJob", jobRepository)
                .start(lastWorkoutReminderChunkStep(dataSourceTransactionManager))
                .build();
    }

    public Step lastWorkoutReminderChunkStep(DataSourceTransactionManager transactionManager) {
        return new StepBuilder("firstStep", jobRepository)
                .<LastUserWorkoutDto, LastUserWorkoutEvent>chunk(3, transactionManager)
                .reader(jdbcCursorItemReader())
                .processor(lastUserWorkoutProcessor)
                .writer(dummyWriter())
                .build();
    }

    public JdbcCursorItemReader<LastUserWorkoutDto> jdbcCursorItemReader() {
        JdbcCursorItemReader<LastUserWorkoutDto> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql(
                "SELECT DISTINCT ON (user_id) user_id as userId, date as dateTime " +
                "FROM workout_data.workout\n" +
                "ORDER BY user_id, date DESC"
        );
        reader.setRowMapper(new LastUserWorkoutDtoRowMapper());
        return reader;
    }

    public ItemWriter<LastUserWorkoutEvent> dummyWriter() {
        return items -> {
            items.forEach(workoutKafkaProducer::sendLastUserWorkoutEvent);
        };
    }
}
