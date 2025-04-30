package com.sourcery.gymapp.backend.workout.batch.reader;

import com.sourcery.gymapp.backend.workout.batch.dto.LastUserWorkoutDto;
import com.sourcery.gymapp.backend.workout.batch.mapper.LastWorkoutReminderDtoRowMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.database.JdbcCursorItemReader;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class WorkoutReaderTest {

    @Mock
    private DataSource dataSource;

    @InjectMocks
    private WorkoutReader workoutReader;

    @Test
    void shouldConfigureJdbcCursorItemReaderCorrectly() {
        JdbcCursorItemReader<LastUserWorkoutDto> reader = workoutReader.lastWorkoutReminderReader();

        assertNotNull(reader);
        assertEquals("SELECT DISTINCT ON (user_id) user_id as userId, date as dateTime " +
                "FROM workout_data.workout\n" +
                "ORDER BY user_id, date DESC", reader.getSql());
    }
}
