package com.bahnrescue.engine.service;

import com.bahnrescue.engine.domain.DelayEvent;
import com.bahnrescue.engine.port.inbound.ProcessDelayEventUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Service implementation for the process delay event use case.
 * Leverages Virtual Threads for high-throughput asynchronous simulation.
 */
@Service
public class AntiGravityNotificationService implements ProcessDelayEventUseCase {

    private static final Logger log = LoggerFactory.getLogger(AntiGravityNotificationService.class);

    // Utilizing Java 21+ Virtual Threads to run async operations
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    @Override
    public CompletableFuture<String> processEvent(DelayEvent event) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("Received event for analysis. Event ID: {}, Commuter: {}", event.eventId(), event.commuterName());

            try {
                // Simulate external LLM prompt generation response time (1 second)
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("LLM client simulation interrupted", e);
            }

            var newDepartureTime = calculateNewDeparture(event.scheduledDeparture(), event.delayMinutes());
            var incidentDescription = formatIncidentType(event.incidentType());

            // Build clear, calm, and actionable notification for someone on a cold platform.
            // Keeping German station/line names intact: S1 (S-Bahn Rhein-Neckar), Edingen-West, Mannheim Hbf.
            var notification = """
                Hello %s, your %s train to %s departing %s at %s is delayed by %d minutes due to %s. \
                It will now depart at %s from Platform %s instead of Platform %s. \
                Please head over to Platform %s to board your train.
                """.formatted(
                    event.commuterName(),
                    event.affectedLine(),
                    event.destination(),
                    event.currentLocation(),
                    event.scheduledDeparture(),
                    event.delayMinutes(),
                    incidentDescription,
                    newDepartureTime,
                    event.newPlatform(),
                    event.originalPlatform(),
                    event.newPlatform()
                ).strip();

            log.info("Successfully generated commuter notification:\n\"\"\"\n{}\n\"\"\"", notification);
            return notification;
        }, executor);
    }

    private String calculateNewDeparture(String scheduledDeparture, int delayMinutes) {
        try {
            var parts = scheduledDeparture.split(":");
            var hours = Integer.parseInt(parts[0]);
            var minutes = Integer.parseInt(parts[1]);
            var totalMinutes = hours * 60 + minutes + delayMinutes;
            var newHours = (totalMinutes / 60) % 24;
            var newMinutes = totalMinutes % 60;
            return String.format("%02d:%02d", newHours, newMinutes);
        } catch (Exception e) {
            log.warn("Could not calculate adjusted departure time for '{}': {}", scheduledDeparture, e.getMessage());
            return scheduledDeparture;
        }
    }

    private String formatIncidentType(String incidentType) {
        if (incidentType == null) {
            return "technical difficulties";
        }
        return switch (incidentType.toUpperCase()) {
            case "SIGNAL_FAILURE" -> "a signal failure";
            case "WEATHER" -> "adverse weather conditions";
            case "TRACK_BLOCKAGE" -> "a track blockage";
            case "TECHNICAL_ISSUE" -> "a technical issue";
            default -> incidentType.toLowerCase().replace('_', ' ');
        };
    }
}
