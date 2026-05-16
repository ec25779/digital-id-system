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

public class DrivingLicenceAuthorityPortal {

    private static final OrganizationId ORGANIZATION_ID = new OrganizationId("driving-licence-authority");

    private final VerificationService verificationService;
    private final LookupService lookupService;

    public DrivingLicenceAuthorityPortal(@NotNull VerificationService verificationService,
                                         @NotNull LookupService lookupService) {
        this.verificationService = verificationService;
        this.lookupService = lookupService;
    }

    public @NotNull LicenceEligibility checkLicenceEligibility(@NotNull UUID identityId,
                                                               @NotNull LicenceType licenceType) {
        if (!verificationService.verifyIdentity(ORGANIZATION_ID, new VerifyIdentityCommand(identityId, VerificationScope.ELIGIBLE))) {
            return LicenceEligibility.INELIGIBLE;
        }

        DigitalId identity;
        try {
            identity = lookupService.lookupIdentity(ORGANIZATION_ID, new LookupIdentityCommand(identityId));
        } catch (IdentityNotFoundException e) {
            return LicenceEligibility.IDENTITY_NOT_FOUND;
        }

        return isOldEnough(identity, licenceType) ? LicenceEligibility.ELIGIBLE : LicenceEligibility.UNDERAGE;
    }

    private boolean isOldEnough(@NotNull DigitalId identity, @NotNull LicenceType licenceType) {
        return Period.between(identity.getDateOfBirth(), LocalDate.now()).getYears() >= licenceType.getMinimumAge();
    }

}
