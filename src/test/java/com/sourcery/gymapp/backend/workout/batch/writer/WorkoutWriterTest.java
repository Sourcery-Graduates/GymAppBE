package com.sourcery.gymapp.backend.workout.batch.writer;

import com.sourcery.gymapp.backend.events.LastUserWorkoutEvent;
import com.sourcery.gymapp.backend.workout.producer.WorkoutKafkaProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import java.time.ZonedDateTime;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class WorkoutWriterTest {

    @Mock
    private WorkoutKafkaProducer workoutKafkaProducer;

    @InjectMocks
    private WorkoutWriter workoutWriter;

    @Test
    void shouldConfigureItemWriterCorrectly() {
        ItemWriter<LastUserWorkoutEvent> itemWriter = workoutWriter.lastWorkoutReminderWriter();

        assertNotNull(itemWriter);
    }

    @Test
    void shouldSendEachEventToKafkaProducer() throws Exception {
        LastUserWorkoutEvent event1 = new LastUserWorkoutEvent(UUID.randomUUID(), ZonedDateTime.now(), 20L);
        LastUserWorkoutEvent event2 = new LastUserWorkoutEvent(UUID.randomUUID(), ZonedDateTime.now(), 20L);

        Chunk<LastUserWorkoutEvent> chunk = new Chunk<>();
        chunk.add(event1);
        chunk.add(event2);

        ItemWriter<LastUserWorkoutEvent> itemWriter = workoutWriter.lastWorkoutReminderWriter();

        itemWriter.write(chunk);

        verify(workoutKafkaProducer).sendLastWorkoutReminderEvent(event1);
        verify(workoutKafkaProducer).sendLastWorkoutReminderEvent(event2);
    }
}
