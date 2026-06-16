package com.bahnrescue.engine.port.outbound;

import com.bahnrescue.engine.domain.DelayEvent;

/**
 * Outbound port interface (driven port) in Hexagonal Architecture.
 * Defines the contract for publishing delay events to external systems.
 */
public interface DelayEventPublisherPort {
    void publishEvent(DelayEvent event);
}
