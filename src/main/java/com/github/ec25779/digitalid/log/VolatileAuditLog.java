package com.github.ec25779.digitalid.log;

import com.github.ec25779.digitalid.auth.OrganizationId;
import org.jetbrains.annotations.NotNull;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class VolatileAuditLog implements AuditLog {

    private final List<AuditEvent> events;
    private final Clock clock;

    public VolatileAuditLog(@NotNull Clock clock) {
        this.clock = clock;
        this.events = new ArrayList<>();
    }

    public VolatileAuditLog() {
        this(Clock.systemUTC());
    }

    @Override
    public void record(@NotNull AuditEvent event) {
        events.add(event);
    }

    @Override
    public @NotNull AuditEvent record(@NotNull UUID identityId, @NotNull OrganizationId caller,
                                      @NotNull AuditAction action) {
        AuditEvent event = new AuditEvent(UUID.randomUUID(), identityId, clock.instant(), caller, action);
        record(event);
        return event;
    }

    @Override
    public @NotNull Optional<AuditEvent> getEvent(@NotNull UUID eventId) {
        return events.stream()
            .filter(event -> event.id().equals(eventId))
            .findFirst();
    }

    @Override
    public @NotNull List<AuditEvent> getEvents(@NotNull UUID identityId) {
        return events.stream()
            .filter(event -> event.identityId().equals(identityId))
            .toList();
    }

    @Override
    public @NotNull List<AuditEvent> getEventsBetween(@NotNull UUID identityId, @NotNull Instant from, @NotNull Instant to) {
        return events.stream()
                .filter(event -> event.identityId().equals(identityId))
                .filter(event -> !event.timestamp().isBefore(from) && !event.timestamp().isAfter(to))
                .toList();
    }

}
