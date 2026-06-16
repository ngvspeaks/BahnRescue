package com.bahnrescue.engine.service;

import com.bahnrescue.engine.domain.DelayEvent;
import com.bahnrescue.engine.port.inbound.PublishDelayEventUseCase;
import com.bahnrescue.engine.port.outbound.DelayEventPublisherPort;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service implementation that orchestrates the execution of the {@link PublishDelayEventUseCase}
 * use case by delegating to the outbound {@link DelayEventPublisherPort}.
 */
@Service
@RequiredArgsConstructor
public class PublishDelayEventService implements PublishDelayEventUseCase {

    private static final Logger log = LoggerFactory.getLogger(PublishDelayEventService.class);

    private final DelayEventPublisherPort delayEventPublisherPort;

    @Override
    public void publish(DelayEvent event) {
        log.info("Orchestrating publish event use case for Event ID: {}", event.eventId());
        delayEventPublisherPort.publishEvent(event);
    }
}
