package com.github.ec25779.digitalid.verification;

import com.github.ec25779.digitalid.auth.OrganizationId;
import org.jetbrains.annotations.NotNull;

public interface VerificationService {

    boolean verifyIdentity(@NotNull OrganizationId caller, @NotNull VerifyIdentityCommand command);

    boolean verifyIdentityBetween(@NotNull OrganizationId caller, @NotNull VerifyIdentityBetweenCommand command);

}
