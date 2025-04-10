package com.sourcery.gymapp.backend.workout.batch.processor;

import com.sourcery.gymapp.backend.events.LastUserWorkoutEvent;
import com.sourcery.gymapp.backend.workout.batch.dto.LastUserWorkoutDto;
import com.sourcery.gymapp.backend.workout.util.DateUtil;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class LastWorkoutReminderProcessor implements ItemProcessor<LastUserWorkoutDto, LastUserWorkoutEvent> {

    @Override
    public LastUserWorkoutEvent process(LastUserWorkoutDto item) throws Exception {
        long daysSinceLastWorkout = DateUtil.calculateDaysSince(item.getDateTime());

        return new LastUserWorkoutEvent(
                item.getUserId(),
                item.getDateTime(),
                daysSinceLastWorkout
        );
    }
}
