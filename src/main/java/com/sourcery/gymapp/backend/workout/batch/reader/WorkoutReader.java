package com.sourcery.gymapp.backend.workout.batch.reader;

import com.sourcery.gymapp.backend.workout.batch.dto.LastUserWorkoutDto;
import com.sourcery.gymapp.backend.workout.batch.mapper.LastWorkoutReminderDtoRowMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@RequiredArgsConstructor
public class WorkoutReader {
    private final DataSource dataSource;

    public JdbcCursorItemReader<LastUserWorkoutDto> lastWorkoutReminderReader() {
        JdbcCursorItemReader<LastUserWorkoutDto> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql(
                "SELECT DISTINCT ON (user_id) user_id as userId, date as dateTime " +
                        "FROM workout_data.workout\n" +
                        "ORDER BY user_id, date DESC"
        );
        reader.setRowMapper(new LastWorkoutReminderDtoRowMapper());
        return reader;
    }
}
