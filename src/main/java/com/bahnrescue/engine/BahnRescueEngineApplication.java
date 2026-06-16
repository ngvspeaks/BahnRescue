package com.bahnrescue.engine;

import com.bahnrescue.engine.adapter.inbound.TrainEventKafkaConsumer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Main application class to bootstrap the Spring Boot BahnRescue Engine.
 */
@SpringBootApplication
public class BahnRescueEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(BahnRescueEngineApplication.class, args);
    }

    /**
     * Simulation runner that passes a sample Kafka payload to the consumer,
     * demonstrating the processing logic and formatted output on startup.
     */
    @Bean
    public CommandLineRunner demoSimulation(TrainEventKafkaConsumer consumer) {
        return args -> {
            var sampleKafkaPayload = """
                {
                  "eventId": "evt_99283f3a",
                  "timestamp": "2026-06-16T17:20:00Z",
                  "commuterName": "Nibin",
                  "affectedLine": "S1 (S-Bahn Rhein-Neckar)",
                  "currentLocation": "Edingen-West",
                  "destination": "Mannheim Hbf",
                  "scheduledDeparture": "17:32",
                  "delayMinutes": 14,
                  "originalPlatform": "1",
                  "newPlatform": "3",
                  "incidentType": "SIGNAL_FAILURE"
                }
                """;

            System.out.println("\n========================================================");
            System.out.println("SIMULATING INCOMING TELEMETRY FROM KAFKA TOPIC");
            System.out.println("========================================================");
            
            consumer.consume(sampleKafkaPayload);
            
            System.out.println("========================================================\n");
        };
    }
}
