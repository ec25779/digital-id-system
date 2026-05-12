package com.github.ec25779.digitalid.service.management;

import com.github.ec25779.digitalid.auth.OrganizationId;
import com.github.ec25779.digitalid.auth.OrganizationPermissionRegistry;
import com.github.ec25779.digitalid.auth.Permission;
import com.github.ec25779.digitalid.service.management.command.CreateIdentityCommand;
import com.github.ec25779.digitalid.service.management.command.RevokeIdentityCommand;
import com.github.ec25779.digitalid.service.management.command.UpdateIdentityCommand;
import com.github.ec25779.digitalid.model.DigitalId;
import org.jetbrains.annotations.NotNull;

public class AuthorizingManagementService implements ManagementService {

    private final ManagementService delegate;
    private final OrganizationPermissionRegistry permissionRegistry;

    public AuthorizingManagementService(@NotNull ManagementService delegate,
                                        @NotNull OrganizationPermissionRegistry permissionRegistry) {
        this.delegate = delegate;
        this.permissionRegistry = permissionRegistry;
    }

    @Override
    public @NotNull DigitalId createIdentity(@NotNull OrganizationId caller, @NotNull CreateIdentityCommand command) {
        permissionRegistry.require(caller, Permission.CREATE_IDENTITY);
        return delegate.createIdentity(caller, command);
    }

    @Override
    public @NotNull DigitalId updateIdentity(@NotNull OrganizationId caller, @NotNull UpdateIdentityCommand command) {
        permissionRegistry.require(caller, Permission.UPDATE_IDENTITY);
        return delegate.updateIdentity(caller, command);
    }

    @Override
    public @NotNull DigitalId revokeIdentity(@NotNull OrganizationId caller, @NotNull RevokeIdentityCommand command) {
        permissionRegistry.require(caller, Permission.REVOKE_IDENTITY);
        return delegate.revokeIdentity(caller, command);
    }

}
