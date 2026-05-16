package com.github.ec25779.digitalid.portal;

import com.github.ec25779.digitalid.auth.OrganizationId;
import com.github.ec25779.digitalid.model.BiologicalSex;
import com.github.ec25779.digitalid.model.DigitalId;
import com.github.ec25779.digitalid.model.IdentityNotFoundException;
import com.github.ec25779.digitalid.service.lookup.LookupService;
import com.github.ec25779.digitalid.service.lookup.command.LookupIdentityCommand;
import com.github.ec25779.digitalid.service.management.ManagementService;
import com.github.ec25779.digitalid.service.management.command.*;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public class CentralAuthorityPortal {

    private static final OrganizationId ORGANIZATION_ID = new OrganizationId("central-authority");

    private final ManagementService managementService;
    private final LookupService lookupService;

    public CentralAuthorityPortal(@NotNull ManagementService managementService, @NotNull LookupService lookupService) {
        this.managementService = managementService;
        this.lookupService = lookupService;
    }

    public @NotNull DigitalId lookupIdentity(@NotNull LookupIdentityCommand command)
        throws IdentityNotFoundException {
        return lookupService.lookupIdentity(ORGANIZATION_ID, command);
    }

    public @NotNull DigitalId createIdentity(@NotNull LocalDate dateOfBirth, @NotNull String placeOfBirth,
                                             @NotNull BiologicalSex biologicalSex, @NotNull String fullName,
                                             @NotNull String address) {
        return managementService.createIdentity(ORGANIZATION_ID,
            new CreateIdentityCommand(dateOfBirth, placeOfBirth, biologicalSex, fullName, address));
    }

    public @NotNull DigitalId updateIdentityFullName(@NotNull UUID identityId, @NotNull String fullName) {
        return managementService.updateIdentity(ORGANIZATION_ID,
            new UpdateIdentityFullNameCommand(identityId, fullName));
    }

    public @NotNull DigitalId updateIdentityAddress(@NotNull UUID identityId, @NotNull String address) {
        return managementService.updateIdentity(ORGANIZATION_ID, new UpdateIdentityAddressCommand(identityId, address));
    }

    public @NotNull DigitalId suspendIdentity(@NotNull UUID identityId) {
        return managementService.updateIdentity(ORGANIZATION_ID, new UpdateIdentitySuspensionCommand(identityId, true));
    }

    public @NotNull DigitalId reinstateIdentity(@NotNull UUID identityId) {
        return managementService.updateIdentity(ORGANIZATION_ID, new UpdateIdentitySuspensionCommand(identityId, false));
    }

    public @NotNull DigitalId revokeIdentity(@NotNull UUID identityId) {
        return managementService.revokeIdentity(ORGANIZATION_ID, new RevokeIdentityCommand(identityId));
    }

}
