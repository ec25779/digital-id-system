package com.github.ec25779.digitalid.log;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuditLog {

    void record(@NotNull AuditEvent event);

    @NotNull Optional<AuditEvent> getEvent(@NotNull UUID eventId);

    @NotNull List<AuditEvent> getEvents(@NotNull UUID identityId);

    @NotNull List<AuditEvent> getEventsBetween(@NotNull UUID identityId, @NotNull Instant from, @NotNull Instant to);

}
