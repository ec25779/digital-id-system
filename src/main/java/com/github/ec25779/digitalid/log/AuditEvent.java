package com.github.ec25779.digitalid.log;

import com.github.ec25779.digitalid.auth.OrganizationId;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.UUID;

public record AuditEvent(@NotNull UUID id, @NotNull UUID identityId, @NotNull Instant timestamp,
                         @NotNull OrganizationId caller, @NotNull AuditAction action) {
}
