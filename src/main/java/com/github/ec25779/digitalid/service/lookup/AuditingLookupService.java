package com.github.ec25779.digitalid.service.lookup;

import com.github.ec25779.digitalid.auth.OrganizationId;
import com.github.ec25779.digitalid.log.AuditAction;
import com.github.ec25779.digitalid.log.AuditLog;
import com.github.ec25779.digitalid.model.DigitalId;
import com.github.ec25779.digitalid.service.lookup.command.LookupIdentityCommand;
import org.jetbrains.annotations.NotNull;

public class AuditingLookupService implements LookupService {

    private final LookupService delegate;
    private final AuditLog auditLog;

    public AuditingLookupService(@NotNull LookupService delegate, @NotNull AuditLog auditLog) {
        this.delegate = delegate;
        this.auditLog = auditLog;
    }

    @Override
    public @NotNull DigitalId lookupIdentity(@NotNull OrganizationId caller, @NotNull LookupIdentityCommand command) {
        DigitalId result = delegate.lookupIdentity(caller, command);
        AuditAction.LookupIdentityAction action = new AuditAction.LookupIdentityAction();

        auditLog.record(result.getId(), caller, action);
        return result;
    }


}
