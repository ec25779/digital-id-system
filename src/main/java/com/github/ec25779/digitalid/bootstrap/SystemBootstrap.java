package com.github.ec25779.digitalid.bootstrap;

import com.github.ec25779.digitalid.auth.OrganizationPermissionRegistry;
import com.github.ec25779.digitalid.auth.Permission;
import com.github.ec25779.digitalid.log.AuditLog;
import com.github.ec25779.digitalid.log.JsonAuditLog;
import com.github.ec25779.digitalid.portal.*;
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
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class SystemBootstrap {

    private static final OrganizationPermissionRegistry PERMISSION_REGISTRY = OrganizationPermissionRegistry.builder()
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
        .grant("health-service",
               Permission.VERIFY_IDENTITY,
               Permission.LOOKUP_IDENTITY
        )
        .build();

    private static final String AUDIT_LOG_PATH = "audit-log.json";
    private static final String IDENTITY_REPO_PATH = "identities.json";

    private final File dataDir;

    private ManagementService managementService;
    private LookupService lookupService;
    private VerificationService verificationService;

    public SystemBootstrap(@NotNull File dataDir) {
        this.dataDir = dataDir;
    }

    public void setup() {
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        AuditLog auditLog = new JsonAuditLog(new File(dataDir, AUDIT_LOG_PATH));
        DigitalIdRepository repository = new JsonDigitalIdRepository(new File(dataDir, IDENTITY_REPO_PATH));

        managementService = new AuthorizingManagementService(new AuditingManagementService(
            new ManagementServiceImpl(repository), auditLog
        ), PERMISSION_REGISTRY);

        lookupService = new AuthorizingLookupService(new AuditingLookupService(
            new LookupServiceImpl(repository), auditLog
        ), PERMISSION_REGISTRY);

        verificationService = new AuthorizingVerificationService(new AuditingVerificationService(
            new VerificationServiceImpl(repository, auditLog), auditLog
        ), PERMISSION_REGISTRY);
    }

    public CentralAuthorityPortal createCentralAuthorityPortal() {
        return new CentralAuthorityPortal(managementService, lookupService);
    }

    public TaxAuthorityPortal createTaxAuthorityPortal() {
        return new TaxAuthorityPortal(verificationService);
    }

    public DrivingLicenceAuthorityPortal createDrivingLicenceAuthorityPortal() {
        return new DrivingLicenceAuthorityPortal(verificationService, lookupService);
    }

    public BankPortal createBankPortal() {
        return new BankPortal(verificationService);
    }

    public HealthServicePortal createHealthServicePortal() {
        return new HealthServicePortal(verificationService, lookupService);
    }

}
