package com.github.ec25779.digitalid.verification;

import com.github.ec25779.digitalid.auth.OrganizationId;
import com.github.ec25779.digitalid.auth.OrganizationPermissionRegistry;
import com.github.ec25779.digitalid.auth.Permission;
import org.jetbrains.annotations.NotNull;

public class AuthorizingVerificationService implements VerificationService {

    private final VerificationService delegate;
    private final OrganizationPermissionRegistry permissionRegistry;

    public AuthorizingVerificationService(@NotNull VerificationService delegate,
                                          @NotNull OrganizationPermissionRegistry permissionRegistry) {
        this.delegate = delegate;
        this.permissionRegistry = permissionRegistry;
    }

    @Override
    public boolean verifyIdentity(@NotNull OrganizationId caller, @NotNull VerifyIdentityCommand command) {
        permissionRegistry.require(caller, Permission.VERIFY_IDENTITY);
        return delegate.verifyIdentity(caller, command);
    }

    @Override
    public boolean verifyIdentityBetween(@NotNull OrganizationId caller, @NotNull VerifyIdentityBetweenCommand command) {
        permissionRegistry.require(caller, Permission.VERIFY_IDENTITY_BETWEEN);
        return delegate.verifyIdentityBetween(caller, command);
    }

}
