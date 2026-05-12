package com.github.ec25779.digitalid.service.lookup;

import com.github.ec25779.digitalid.auth.OrganizationId;
import com.github.ec25779.digitalid.auth.OrganizationPermissionRegistry;
import com.github.ec25779.digitalid.auth.Permission;
import com.github.ec25779.digitalid.model.DigitalId;
import com.github.ec25779.digitalid.service.lookup.command.LookupIdentityCommand;
import com.github.ec25779.digitalid.service.management.ManagementService;
import com.github.ec25779.digitalid.service.management.command.CreateIdentityCommand;
import com.github.ec25779.digitalid.service.management.command.RevokeIdentityCommand;
import com.github.ec25779.digitalid.service.management.command.UpdateIdentityCommand;
import org.jetbrains.annotations.NotNull;

public class AuthorizingLookupService implements LookupService {

    private final LookupService delegate;
    private final OrganizationPermissionRegistry permissionRegistry;

    public AuthorizingLookupService(@NotNull LookupService delegate,
                                    @NotNull OrganizationPermissionRegistry permissionRegistry) {
        this.delegate = delegate;
        this.permissionRegistry = permissionRegistry;
    }

    @Override
    public @NotNull DigitalId lookupIdentity(@NotNull OrganizationId caller, @NotNull LookupIdentityCommand command) {
        permissionRegistry.require(caller, Permission.LOOKUP_IDENTITY);
        return delegate.lookupIdentity(caller, command);
    }

}
