package com.github.ec25779.digitalid.portal;

import com.github.ec25779.digitalid.auth.OrganizationId;
import com.github.ec25779.digitalid.service.verification.VerificationScope;
import com.github.ec25779.digitalid.service.verification.VerificationService;
import com.github.ec25779.digitalid.service.verification.command.VerifyIdentityBetweenCommand;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.UUID;

public class TaxAuthorityPortal {

    private static final OrganizationId ORGANIZATION_ID = new OrganizationId("tax-authority");

    private static final int TAX_YEAR_START_MONTH = 4;
    private static final int TAX_YEAR_START_DAY = 6;

    private final VerificationService verificationService;

    public TaxAuthorityPortal(@NotNull VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    public boolean verifyIdentityForTaxYear(@NotNull UUID identityId, int year) {
        Instant from = getTaxYearStart(year);
        Instant to = getTaxYearStart(year + 1);
        return verificationService.verifyIdentityBetween(ORGANIZATION_ID,
            new VerifyIdentityBetweenCommand(identityId, VerificationScope.ELIGIBLE, from, to));
    }

    public boolean verifyIdentityForCurrentTaxYear(@NotNull UUID identityId) {
        LocalDate today = LocalDate.now();
        int startYear = today.isBefore(LocalDate.of(today.getYear(), TAX_YEAR_START_MONTH, TAX_YEAR_START_DAY)) ?
            today.getYear() - 1 : today.getYear();
        return verifyIdentityForTaxYear(identityId, startYear);
    }

    private static @NotNull Instant getTaxYearStart(int startYear) {
        return LocalDate.of(startYear, TAX_YEAR_START_MONTH, TAX_YEAR_START_DAY)
            .atStartOfDay(ZoneOffset.UTC)
            .toInstant();
    }

}
