package com.sourcery.gymapp.backend.workout.batch.processor;

import com.sourcery.gymapp.backend.events.LastUserWorkoutEvent;
import com.sourcery.gymapp.backend.workout.batch.dto.LastUserWorkoutDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import java.time.ZonedDateTime;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class LastWorkoutReminderProcessorTest {

    private LastWorkoutReminderProcessor processor;
    private UUID userId;

    @BeforeEach
    void setup() {
        processor = new LastWorkoutReminderProcessor();
        userId = UUID.fromString("4057c07b-dd9c-4cc1-ad0c-edebb241b921");
    }

    @Test
    void shouldReturnNullIfWorkoutIsRecent() throws Exception {
        LastUserWorkoutDto recentWorkout = new LastUserWorkoutDto();
        recentWorkout.setUserId(userId);
        recentWorkout.setDateTime(ZonedDateTime.now().minusDays(5));

        LastUserWorkoutEvent result = processor.process(recentWorkout);

        assertNull(result);
    }

    @Test
    void shouldReturnEventIfWorkoutIsOld() throws Exception {
        LastUserWorkoutDto oldWorkout = new LastUserWorkoutDto();
        oldWorkout.setUserId(userId);
        oldWorkout.setDateTime(ZonedDateTime.now().minusDays(20));

        LastUserWorkoutEvent result = processor.process(oldWorkout);

        assertNotNull(result);
        assertEquals(userId, result.userId());
        assertEquals(20, result.daysSinceLastWorkout());
    }
}
