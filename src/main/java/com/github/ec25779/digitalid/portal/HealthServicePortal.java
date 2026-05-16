package com.github.ec25779.digitalid.portal;

import com.github.ec25779.digitalid.auth.OrganizationId;
import com.github.ec25779.digitalid.model.DigitalId;
import com.github.ec25779.digitalid.model.IdentityNotFoundException;
import com.github.ec25779.digitalid.service.lookup.LookupService;
import com.github.ec25779.digitalid.service.lookup.command.LookupIdentityCommand;
import com.github.ec25779.digitalid.service.verification.VerificationScope;
import com.github.ec25779.digitalid.service.verification.VerificationService;
import com.github.ec25779.digitalid.service.verification.command.VerifyIdentityCommand;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;

public class HealthServicePortal {

    private static final OrganizationId ORGANIZATION_ID = new OrganizationId("health-service");

    private static final int ELDERLY_SERVICES_MINIMUM_AGE = 60;

    private final VerificationService verificationService;
    private final LookupService lookupService;

    public HealthServicePortal(@NotNull VerificationService verificationService, @NotNull LookupService lookupService) {
        this.verificationService = verificationService;
        this.lookupService = lookupService;
    }

    public boolean verifyIdentity(@NotNull UUID identityId) {
        return verificationService.verifyIdentity(ORGANIZATION_ID,
            new VerifyIdentityCommand(identityId, VerificationScope.VALID));
    }

    public boolean verifyIdentityEligibleForElderlyServices(@NotNull UUID identityId) {
        if (!verifyIdentity(identityId)) {
            return false;
        }

        try {
            DigitalId identity = lookupService.lookupIdentity(ORGANIZATION_ID, new LookupIdentityCommand(identityId));
            return Period.between(identity.getDateOfBirth(), LocalDate.now()).getYears() >= ELDERLY_SERVICES_MINIMUM_AGE;
        } catch (IdentityNotFoundException e) {
            return false;
        }
    }

}
