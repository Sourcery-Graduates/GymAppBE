package com.sourcery.gymapp.backend.authentication.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sourcery.gymapp.backend.authentication.service.LastUserWorkoutService;
import com.sourcery.gymapp.backend.events.LastUserWorkoutEvent;
import com.sourcery.gymapp.backend.globalconfig.KafkaProcessingContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthenticationKafkaConsumer {
    private final ObjectMapper objectMapper;
    private final LastUserWorkoutService service;

    @KafkaListener(topics = "${spring.kafka.topics.last-user-workout}", groupId = "authentication-listener-group")
    public void onLastUserWorkoutMessage(ConsumerRecord<UUID, String> record) {
        try {
            KafkaProcessingContext.enableKafkaProcessing();

            var data = objectMapper.readValue(record.value(), LastUserWorkoutEvent.class);
            service.sendLastWorkoutReminder(data);
            log.info("Last user workout event processed: {}", record.key());
        } catch (Exception e) {
            log.error("Error processing last user workout event: {}", e.getMessage(), e);
        } finally {
            KafkaProcessingContext.disableKafkaProcessing();
        }
    }
}
