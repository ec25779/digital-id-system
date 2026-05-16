package com.github.ec25779.digitalid.portal;

import com.github.ec25779.digitalid.auth.OrganizationId;
import com.github.ec25779.digitalid.service.verification.VerificationScope;
import com.github.ec25779.digitalid.service.verification.VerificationService;
import com.github.ec25779.digitalid.service.verification.command.VerifyIdentityCommand;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class BankPortal {

    private static final OrganizationId ORGANIZATION_ID = new OrganizationId("bank");

    private final VerificationService verificationService;

    public BankPortal(@NotNull VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    public boolean verifyIdentityExists(@NotNull UUID identityId) {
        return verificationService.verifyIdentity(ORGANIZATION_ID,
            new VerifyIdentityCommand(identityId, VerificationScope.VALID));
    }

    public boolean verifyIdentityEligibleForLoan(@NotNull UUID identityId) {
        return verificationService.verifyIdentity(ORGANIZATION_ID,
            new VerifyIdentityCommand(identityId, VerificationScope.ELIGIBLE));
    }

}
