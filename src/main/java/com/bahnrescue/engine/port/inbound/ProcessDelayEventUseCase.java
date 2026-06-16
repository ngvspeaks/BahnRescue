package com.bahnrescue.engine.port.inbound;

import com.bahnrescue.engine.domain.DelayEvent;
import java.util.concurrent.CompletableFuture;

/**
 * Inbound port interface (driver port) in Hexagonal Architecture.
 * Defines the use case for processing incoming delay events.
 */
public interface ProcessDelayEventUseCase {
    CompletableFuture<String> processEvent(DelayEvent event);
}
