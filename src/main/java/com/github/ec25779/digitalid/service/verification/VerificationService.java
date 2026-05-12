package com.github.ec25779.digitalid.service.verification;

import com.github.ec25779.digitalid.auth.OrganizationId;
import com.github.ec25779.digitalid.service.verification.command.VerifyIdentityBetweenCommand;
import com.github.ec25779.digitalid.service.verification.command.VerifyIdentityCommand;
import org.jetbrains.annotations.NotNull;

public interface VerificationService {

    boolean verifyIdentity(@NotNull OrganizationId caller, @NotNull VerifyIdentityCommand command);

    boolean verifyIdentityBetween(@NotNull OrganizationId caller, @NotNull VerifyIdentityBetweenCommand command);

}
