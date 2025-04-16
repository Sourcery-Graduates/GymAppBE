package com.sourcery.gymapp.backend.workout.batch.writer;

import com.sourcery.gymapp.backend.events.LastUserWorkoutEvent;
import com.sourcery.gymapp.backend.workout.producer.WorkoutKafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WorkoutWriter {
    private final WorkoutKafkaProducer workoutKafkaProducer;

    public ItemWriter<LastUserWorkoutEvent> lastWorkoutReminderWriter() {
        return items -> {
            items.forEach(workoutKafkaProducer::sendLastWorkoutReminderEvent);
        };
    }
}
