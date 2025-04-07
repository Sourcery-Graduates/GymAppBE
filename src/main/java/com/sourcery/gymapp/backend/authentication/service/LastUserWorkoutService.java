package com.sourcery.gymapp.backend.authentication.service;

import com.sourcery.gymapp.backend.authentication.exception.AuthenticationRuntimeException;
import com.sourcery.gymapp.backend.authentication.mapper.EmailTemplateMapper;
import com.sourcery.gymapp.backend.authentication.model.User;
import com.sourcery.gymapp.backend.authentication.producer.AuthKafkaProducer;
import com.sourcery.gymapp.backend.authentication.repository.UserRepository;
import com.sourcery.gymapp.backend.events.LastUserWorkoutEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LastUserWorkoutService {
    private final UserRepository userRepository;
    private final AuthKafkaProducer authKafkaProducer;
    private final EmailTemplateMapper emailTemplateMapper;

    public void sendLastWorkoutReminder(LastUserWorkoutEvent event) {
        User user = getUser(event);

        authKafkaProducer.sendEmailEvent(emailTemplateMapper.toLastWorkoutReminderEmail(event, user), user.getId());
    }

    private User getUser(LastUserWorkoutEvent event) {
        return userRepository.findById(event.userId())
                .orElseThrow(() -> new AuthenticationRuntimeException("User doesn't have an email"));
    }
}
