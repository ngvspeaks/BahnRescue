package com.bahnrescue.engine.port.inbound;

import com.bahnrescue.engine.domain.DelayEvent;

/**
 * Inbound port interface (driver port) in Hexagonal Architecture.
 * Defines the use case for manually publishing delay events.
 */
public interface PublishDelayEventUseCase {
    void publish(DelayEvent event);
}
