package com.bahnrescue.engine;

import com.bahnrescue.engine.domain.DelayEvent;
import com.bahnrescue.engine.service.AntiGravityNotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.kafka.listener.auto-startup=false")
class BahnRescueEngineApplicationTests {

    @Autowired
    private AntiGravityNotificationService notificationService;

    @Test
    void contextLoads() {
        assertNotNull(notificationService);
    }

    @Test
    void testNotificationGeneration() throws ExecutionException, InterruptedException {
        var event = new DelayEvent(
            "evt_test123",
            "2026-06-16T17:20:00Z",
            "Nibin",
            "S1 (S-Bahn Rhein-Neckar)",
            "Edingen-West",
            "Mannheim Hbf",
            "17:32",
            14,
            "1",
            "3",
            "SIGNAL_FAILURE"
        );

        var future = notificationService.processEvent(event);
        var message = future.get();

        assertNotNull(message);
        assertTrue(message.contains("Hello Nibin"));
        assertTrue(message.contains("S1 (S-Bahn Rhein-Neckar)"));
        assertTrue(message.contains("delayed by 14 minutes"));
        assertTrue(message.contains("Platform 3 instead of Platform 1"));
        assertTrue(message.contains("17:46")); // 17:32 + 14 mins
    }
}
