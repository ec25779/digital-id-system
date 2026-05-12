package com.github.ec25779.digitalid.service.management;

import com.github.ec25779.digitalid.auth.OrganizationId;
import com.github.ec25779.digitalid.service.management.command.CreateIdentityCommand;
import com.github.ec25779.digitalid.service.management.command.RevokeIdentityCommand;
import com.github.ec25779.digitalid.service.management.command.UpdateIdentityCommand;
import com.github.ec25779.digitalid.model.DigitalId;
import org.jetbrains.annotations.NotNull;

public interface ManagementService {

    @NotNull DigitalId createIdentity(@NotNull OrganizationId caller, @NotNull CreateIdentityCommand command);

    @NotNull DigitalId updateIdentity(@NotNull OrganizationId caller, @NotNull UpdateIdentityCommand command);

    @NotNull DigitalId revokeIdentity(@NotNull OrganizationId caller, @NotNull RevokeIdentityCommand command);

}
