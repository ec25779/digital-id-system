package com.github.ec25779.digitalid.service.management;

import com.github.ec25779.digitalid.auth.OrganizationId;
import com.github.ec25779.digitalid.log.AuditAction;
import com.github.ec25779.digitalid.log.AuditLog;
import com.github.ec25779.digitalid.model.DigitalId;
import com.github.ec25779.digitalid.model.DigitalIdStatus;
import com.github.ec25779.digitalid.service.management.command.*;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class AuditingManagementService implements ManagementService {

    private final ManagementService delegate;
    private final AuditLog auditLog;

    public AuditingManagementService(@NotNull ManagementService delegate, @NotNull AuditLog auditLog) {
        this.delegate = delegate;
        this.auditLog = auditLog;
    }

    @Override
    public @NotNull DigitalId createIdentity(@NotNull OrganizationId caller, @NotNull CreateIdentityCommand command) {
        DigitalId result = delegate.createIdentity(caller, command);
        AuditAction.CreateIdentityAction action = new AuditAction.CreateIdentityAction(
            result.getDateOfBirth(), result.getPlaceOfBirth(), result.getBiologicalSex(),
            result.getFullName(), result.getAddress(), result.getStatus()
        );

        auditLog.record(result.getId(), caller, action);
        return result;
    }

    @Override
    public @NotNull DigitalId updateIdentity(@NotNull OrganizationId caller, @NotNull UpdateIdentityCommand command) {
        DigitalId result = delegate.updateIdentity(caller, command);
        AuditAction action = switch (command) {
            case UpdateIdentityFullNameCommand(UUID _, String fullName) -> new AuditAction.UpdateIdentityFullNameAction(fullName);
            case UpdateIdentityAddressCommand(UUID _, String address) -> new AuditAction.UpdateIdentityAddressAction(address);
            case UpdateIdentitySuspensionCommand(UUID _, boolean suspended) -> new AuditAction.UpdateIdentityStatusAction(suspended ?
                DigitalIdStatus.SUSPENDED : DigitalIdStatus.ACTIVE);
        };

        auditLog.record(result.getId(), caller, action);
        return result;
    }

    @Override
    public @NotNull DigitalId revokeIdentity(@NotNull OrganizationId caller, @NotNull RevokeIdentityCommand command) {
        DigitalId result = delegate.revokeIdentity(caller, command);
        auditLog.record(result.getId(), caller, new AuditAction.UpdateIdentityStatusAction(DigitalIdStatus.REVOKED));
        return result;
    }

}
