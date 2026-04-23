package com.github.ec25779.digitalid.central;

import com.github.ec25779.digitalid.auth.OrganizationId;
import com.github.ec25779.digitalid.auth.OrganizationPermissionRegistry;
import com.github.ec25779.digitalid.auth.Permission;
import com.github.ec25779.digitalid.model.DigitalId;
import org.jetbrains.annotations.NotNull;

public class AuthorizingIdentityManager implements DigitalIdentityManager {

    private final DigitalIdentityManager delegate;
    private final OrganizationPermissionRegistry permissionRegistry;

    public AuthorizingIdentityManager(@NotNull DigitalIdentityManager delegate,
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

}
