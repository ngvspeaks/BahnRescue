package com.bahnrescue.engine.adapter.outbound;

import com.bahnrescue.engine.domain.DelayEvent;
import com.bahnrescue.engine.port.outbound.DelayEventPublisherPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Outbound adapter (driven adapter) that implements the {@link DelayEventPublisherPort}
 * to publish delay event telemetry directly to an Apache Kafka topic.
 */
@Component
@RequiredArgsConstructor
public class KafkaDelayEventPublisher implements DelayEventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(KafkaDelayEventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void publishEvent(DelayEvent event) {
        log.info("Outbound adapter publishing delay event: {}", event.eventId());
        try {
            // Serialize to JSON string matching the format expected by the consumer
            String jsonPayload = objectMapper.writeValueAsString(event);
            
            // Publish to the "train-delay-events" topic using the eventId as the partition key
            kafkaTemplate.send("train-delay-events", event.eventId(), jsonPayload);
            
            log.info("Successfully published event {} to Kafka topic", event.eventId());
        } catch (Exception e) {
            log.error("Failed to publish event {} to Kafka topic: {}", event.eventId(), e.getMessage(), e);
            throw new RuntimeException("Failed to publish event to Kafka", e);
        }
    }
}
