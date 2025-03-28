package com.sourcery.gymapp.backend.workout.batch.job;

import com.sourcery.gymapp.backend.workout.batch.dto.LastUserWorkoutDto;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@RequiredArgsConstructor
public class JobConfig {
    private final JobRepository jobRepository;
    private final DataSource dataSource;
    private final DataSourceTransactionManager dataSourceTransactionManager;

    @Bean
    public Job remindUserJob() {
        return new JobBuilder("remindUserJob", jobRepository)
                .start(firstStep(dataSourceTransactionManager))
                .build();
    }


    public Step firstStep(DataSourceTransactionManager transactionManager) {
        return new StepBuilder("firstStep", jobRepository)
                .<LastUserWorkoutDto, LastUserWorkoutDto>chunk(3, transactionManager)
                .reader(jdbcCursorItemReader())
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
        //TODO create custom row mapper because of zoneddatetime
        BeanPropertyRowMapper<LastUserWorkoutDto> beanPropertyRowMapper = new BeanPropertyRowMapper<>();
        beanPropertyRowMapper.setMappedClass(LastUserWorkoutDto.class);
        reader.setRowMapper(beanPropertyRowMapper);
        return reader;
    }

    public ItemWriter<LastUserWorkoutDto> dummyWriter() {
        return items -> {
            System.out.println("Inside Dummy Writer");
            items.forEach(item -> System.out.println("Processed item: " + item));
        };
    }
}
