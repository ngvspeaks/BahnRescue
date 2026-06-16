package com.bahnrescue.engine.domain;

/**
 * Domain record representing a delay event payload in the transit system.
 */
public record DelayEvent(
    String eventId,
    String timestamp,
    String commuterName,
    String affectedLine,
    String currentLocation,
    String destination,
    String scheduledDeparture,
    int delayMinutes,
    String originalPlatform,
    String newPlatform,
    String incidentType
) {}
