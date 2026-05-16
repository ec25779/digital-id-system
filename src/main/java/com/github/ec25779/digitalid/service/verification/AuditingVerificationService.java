package com.github.ec25779.digitalid.service.verification;

import com.github.ec25779.digitalid.auth.OrganizationId;
import com.github.ec25779.digitalid.log.AuditAction;
import com.github.ec25779.digitalid.log.AuditLog;
import com.github.ec25779.digitalid.service.verification.command.VerifyIdentityBetweenCommand;
import com.github.ec25779.digitalid.service.verification.command.VerifyIdentityCommand;
import org.jetbrains.annotations.NotNull;

public class AuditingVerificationService implements VerificationService {

    private final VerificationService delegate;
    private final AuditLog auditLog;

    public AuditingVerificationService(@NotNull VerificationService delegate, @NotNull AuditLog auditLog) {
        this.delegate = delegate;
        this.auditLog = auditLog;
    }

    @Override
    public boolean verifyIdentity(@NotNull OrganizationId caller, @NotNull VerifyIdentityCommand command) {
        boolean result = delegate.verifyIdentity(caller, command);
        auditLog.record(command.id(), caller, new AuditAction.VerifyIdentityAction(command.scope()));
        return result;
    }

    @Override
    public boolean verifyIdentityBetween(@NotNull OrganizationId caller,
                                         @NotNull VerifyIdentityBetweenCommand command) {
        boolean result = delegate.verifyIdentityBetween(caller, command);
        auditLog.record(command.id(), caller,
            new AuditAction.VerifyIdentityBetweenAction(command.scope(), command.from(), command.to()));
        return result;
    }

}
