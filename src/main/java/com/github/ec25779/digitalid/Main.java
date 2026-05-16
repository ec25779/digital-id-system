package com.github.ec25779.digitalid;

import com.github.ec25779.digitalid.auth.OrganizationPermissionRegistry;
import com.github.ec25779.digitalid.auth.Permission;
import com.github.ec25779.digitalid.log.AuditLog;
import com.github.ec25779.digitalid.log.JsonAuditLog;
import com.github.ec25779.digitalid.portal.BankPortal;
import com.github.ec25779.digitalid.portal.CentralAuthorityPortal;
import com.github.ec25779.digitalid.portal.DrivingLicenceAuthorityPortal;
import com.github.ec25779.digitalid.portal.LicenceEligibility;
import com.github.ec25779.digitalid.portal.LicenceType;
import com.github.ec25779.digitalid.portal.TaxAuthorityPortal;
import com.github.ec25779.digitalid.repository.DigitalIdRepository;
import com.github.ec25779.digitalid.repository.JsonDigitalIdRepository;
import com.github.ec25779.digitalid.service.lookup.AuditingLookupService;
import com.github.ec25779.digitalid.service.lookup.AuthorizingLookupService;
import com.github.ec25779.digitalid.service.lookup.LookupService;
import com.github.ec25779.digitalid.service.lookup.LookupServiceImpl;
import com.github.ec25779.digitalid.service.management.AuditingManagementService;
import com.github.ec25779.digitalid.service.management.AuthorizingManagementService;
import com.github.ec25779.digitalid.service.management.ManagementService;
import com.github.ec25779.digitalid.service.management.ManagementServiceImpl;
import com.github.ec25779.digitalid.service.verification.AuditingVerificationService;
import com.github.ec25779.digitalid.service.verification.AuthorizingVerificationService;
import com.github.ec25779.digitalid.service.verification.VerificationService;
import com.github.ec25779.digitalid.service.verification.VerificationServiceImpl;

import java.io.File;
import java.util.UUID;

public class Main {

    public static void main(String[] args) {
        OrganizationPermissionRegistry permissionRegistry = OrganizationPermissionRegistry.builder()
            .grant("central-authority",
                Permission.CREATE_IDENTITY,
                Permission.UPDATE_IDENTITY,
                Permission.REVOKE_IDENTITY,
                Permission.LOOKUP_IDENTITY
            )
            .grant("tax-authority",
                Permission.VERIFY_IDENTITY_BETWEEN
            )
            .grant("driving-licence-authority",
                Permission.VERIFY_IDENTITY,
                Permission.LOOKUP_IDENTITY
            )
            .grant("bank",
                Permission.VERIFY_IDENTITY
            )
            .build();

        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        AuditLog auditLog = new JsonAuditLog(new File(dataDir, "audit-log.json"));
        DigitalIdRepository repository = new JsonDigitalIdRepository(new File(dataDir, "identities.json"));

        ManagementService managementService = new AuthorizingManagementService(new AuditingManagementService(
            new ManagementServiceImpl(repository), auditLog
        ), permissionRegistry);

        LookupService lookupService = new AuthorizingLookupService(new AuditingLookupService(
            new LookupServiceImpl(repository), auditLog
        ), permissionRegistry);

        VerificationService verificationService = new AuthorizingVerificationService(new AuditingVerificationService(
            new VerificationServiceImpl(repository, auditLog), auditLog
        ), permissionRegistry);

        CentralAuthorityPortal centralAuthorityPortal = new CentralAuthorityPortal(managementService, lookupService);
        TaxAuthorityPortal hmrcPortal = new TaxAuthorityPortal(verificationService);
        DrivingLicenceAuthorityPortal dvlaPortal = new DrivingLicenceAuthorityPortal(verificationService, lookupService);
        BankPortal bankPortal = new BankPortal(verificationService);

//        DigitalId created = centralAuthorityPortal.createIdentity(
//            LocalDate.of(1990, 1, 1), "London", BiologicalSex.MALE, "John Doe", "123 Main St");
//        System.out.println("Created: " + created);
//
//        DigitalId looked = centralAuthorityPortal.lookupIdentity(created.getId());
//        System.out.println("Looked up: " + looked);

        LicenceEligibility eligibility = dvlaPortal.checkLicenceEligibility(UUID.fromString("04a46213-a9ba-4c45-87f9-7b0dfaedf14c"), LicenceType.FULL);
        System.out.println("Full licence eligibility: " + eligibility);

        // TODO select portal and interact with commands
    }

}
