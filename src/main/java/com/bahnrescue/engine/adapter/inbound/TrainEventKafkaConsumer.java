package com.bahnrescue.engine.adapter.inbound;

import com.bahnrescue.engine.domain.DelayEvent;
import com.bahnrescue.engine.port.inbound.ProcessDelayEventUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Inbound adapter (driver adapter) that listens to a Kafka topic,
 * deserializes the JSON payload into domain record, and invokes the use case.
 */
@Component
@RequiredArgsConstructor
public class TrainEventKafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(TrainEventKafkaConsumer.class);

    private final ProcessDelayEventUseCase processDelayEventUseCase;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "train-delay-events", groupId = "bahnrescue-notification-group")
    public void consume(String rawPayload) {
        log.info("Consuming raw Kafka delay event payload: {}", rawPayload);
        try {
            // Deserialize JSON to DelayEvent domain record
            var event = objectMapper.readValue(rawPayload, DelayEvent.class);
            log.info("Parsed telemetry event successfully. ID: {}, Commuter: {}", event.eventId(), event.commuterName());

            // Process event asynchronously via Inbound Port
            processDelayEventUseCase.processEvent(event)
                .thenAccept(notification -> log.info("Successfully finished delay notification processing pipeline for event {}", event.eventId()))
                .exceptionally(ex -> {
                    log.error("Error occurred while executing pipeline for event {}: {}", event.eventId(), ex.getMessage(), ex);
                    return null;
                });

        } catch (Exception e) {
            log.error("Failed to process inbound telemetry event payload due to deserialization error: {}", e.getMessage(), e);
        }
    }
}
