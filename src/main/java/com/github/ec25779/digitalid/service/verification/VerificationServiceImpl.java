package com.github.ec25779.digitalid.service.verification;

import com.github.ec25779.digitalid.auth.OrganizationId;
import com.github.ec25779.digitalid.log.AuditAction;
import com.github.ec25779.digitalid.log.AuditEvent;
import com.github.ec25779.digitalid.log.AuditLog;
import com.github.ec25779.digitalid.model.DigitalId;
import com.github.ec25779.digitalid.model.DigitalIdStatus;
import com.github.ec25779.digitalid.repository.DigitalIdRepository;
import com.github.ec25779.digitalid.service.verification.command.VerifyIdentityBetweenCommand;
import com.github.ec25779.digitalid.service.verification.command.VerifyIdentityCommand;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class VerificationServiceImpl implements VerificationService {

    private final DigitalIdRepository repository;
    private final AuditLog auditLog;

    public VerificationServiceImpl(@NotNull DigitalIdRepository repository, @NotNull AuditLog auditLog) {
        this.repository = repository;
        this.auditLog = auditLog;
    }

    private boolean passesScope(DigitalIdStatus status, VerificationScope scope) {
        return switch (scope) {
            case VALID -> status == DigitalIdStatus.ACTIVE || status == DigitalIdStatus.SUSPENDED;
            case ELIGIBLE -> status == DigitalIdStatus.ACTIVE;
        };
    }

    @Override
    public boolean verifyIdentity(@NotNull OrganizationId caller, @NotNull VerifyIdentityCommand command) {
        Optional<DigitalId> result = repository.find(command.id());
        if (result.isEmpty()) {
            return false;
        }

        DigitalIdStatus status = result.get().getStatus();
        return passesScope(status, command.scope());
    }

    @Override
    public boolean verifyIdentityBetween(@NotNull OrganizationId caller, @NotNull VerifyIdentityBetweenCommand command) {
        UUID id = command.id();
        if (repository.find(id).isEmpty()) {
            return false;
        }

        Instant from = command.from();
        Instant to = command.to();

        List<AuditEvent> allEvents = auditLog.getEvents(id);

        Optional<AuditEvent> lastBeforeFrom = allEvents.stream()
            .filter(e -> !e.timestamp().isAfter(from))
            .filter(e -> e.action() instanceof AuditAction.CreateIdentityAction || e.action() instanceof AuditAction.UpdateIdentityStatusAction)
            .reduce((_, s) -> s);

        if (lastBeforeFrom.isEmpty()) {
            return false;
        }

        DigitalIdStatus statusAtFrom = switch (lastBeforeFrom.get().action()) {
            case AuditAction.CreateIdentityAction action -> action.status();
            case AuditAction.UpdateIdentityStatusAction action -> action.status();
            default -> throw new IllegalStateException();
        };

        VerificationScope scope = command.scope();
        if (!passesScope(statusAtFrom, scope)) {
            return false;
        }

        return allEvents.stream()
            .filter(e -> e.timestamp().isAfter(from) && !e.timestamp().isAfter(to))
            .filter(e -> e.action() instanceof AuditAction.UpdateIdentityStatusAction(DigitalIdStatus status) && !passesScope(status, scope))
            .findAny()
            .isEmpty();
    }

}
