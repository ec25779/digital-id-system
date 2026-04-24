package com.github.ec25779.digitalid.central;

import com.github.ec25779.digitalid.auth.OrganizationId;
import com.github.ec25779.digitalid.model.DigitalId;
import org.jetbrains.annotations.NotNull;

public interface DigitalIdentityManager {

    @NotNull DigitalId createIdentity(@NotNull OrganizationId caller, @NotNull CreateIdentityCommand command);

    @NotNull DigitalId updateIdentity(@NotNull OrganizationId caller, @NotNull UpdateIdentityCommand command);

    @NotNull DigitalId revokeIdentity(@NotNull OrganizationId caller, @NotNull RevokeIdentityCommand command);

}
