package com.bahnrescue.engine.adapter.inbound;

import com.bahnrescue.engine.domain.DelayEvent;
import com.bahnrescue.engine.port.inbound.PublishDelayEventUseCase;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller to manually trigger and audit simulated transit events.
 */
@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
public class BahnRescueTestController {

    private static final Logger log = LoggerFactory.getLogger(BahnRescueTestController.class);

    private final PublishDelayEventUseCase publishDelayEventUseCase;

    @PostMapping("/event")
    public ResponseEntity<Map<String, String>> publishEvent(@RequestBody DelayEvent event) {
        log.info("Received HTTP request to publish test delay event: {}", event.eventId());
        try {
            publishDelayEventUseCase.publish(event);
            log.info("Successfully processed and published test delay event: {}", event.eventId());
            
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body(Map.of(
                            "status", "Accepted",
                            "message", "Transit event with ID " + event.eventId() + " successfully published.",
                            "eventId", event.eventId()
                    ));
        } catch (Exception e) {
            log.error("Failed to publish test delay event {}: {}", event.eventId(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", "Error",
                            "message", "Failed to publish event: " + e.getMessage()
                    ));
        }
    }
}
