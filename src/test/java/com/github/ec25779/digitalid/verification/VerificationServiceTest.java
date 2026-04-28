package com.github.ec25779.digitalid.verification;

import com.github.ec25779.digitalid.auth.OrganizationId;
import com.github.ec25779.digitalid.log.AuditAction;
import com.github.ec25779.digitalid.log.AuditEvent;
import com.github.ec25779.digitalid.log.AuditLog;
import com.github.ec25779.digitalid.log.VolatileAuditLog;
import com.github.ec25779.digitalid.model.BiologicalSex;
import com.github.ec25779.digitalid.model.DigitalId;
import com.github.ec25779.digitalid.model.DigitalIdStatus;
import com.github.ec25779.digitalid.repository.DigitalIdRepository;
import com.github.ec25779.digitalid.repository.VolatileDigitalIdRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VerificationServiceTest {

    private static final OrganizationId CALLER = new OrganizationId("tax-authority");
    private static final LocalDate DATE_OF_BIRTH = LocalDate.of(2000, 1, 1);

    private static final Instant CREATED_AT = Instant.parse("2024-01-01T00:00:00Z");
    private static final Instant BEFORE_PERIOD = Instant.parse("2024-02-01T00:00:00Z");
    private static final Instant PERIOD_FROM = Instant.parse("2024-04-01T00:00:00Z");
    private static final Instant MID_PERIOD = Instant.parse("2024-07-01T00:00:00Z");
    private static final Instant PERIOD_TO = Instant.parse("2024-12-31T00:00:00Z");
    private static final Instant AFTER_PERIOD = Instant.parse("2025-03-01T00:00:00Z");

    private DigitalIdRepository repository;
    private AuditLog auditLog;
    private VerificationService verificationService;

    @BeforeEach
    public void setUp() {
        Clock clock = Clock.fixed(CREATED_AT, ZoneOffset.UTC);
        repository = new VolatileDigitalIdRepository();
        auditLog = new VolatileAuditLog(clock);
        verificationService = new VerificationServiceImpl(repository, auditLog);
    }

    @Test
    public void testUnknownIdentityFailEligible() {
        assertVerifyFail(new VerifyIdentityCommand(UUID.randomUUID(), VerificationScope.ELIGIBLE));
    }

    @Test
    public void testActiveIdentityPassEligible() {
        DigitalId digitalId = createIdentity();
        assertVerifyPass(new VerifyIdentityCommand(digitalId.getId(), VerificationScope.ELIGIBLE));
    }

    @Test
    public void testSuependedIdentityFailEligible() {
        DigitalId digitalId = createIdentity();
        digitalId.suspend();
        repository.save(digitalId);
        assertVerifyFail(new VerifyIdentityCommand(digitalId.getId(), VerificationScope.ELIGIBLE));
    }

    @Test
    public void testSuspendedIdentityPassValid() {
        DigitalId digitalId = createIdentity();
        digitalId.suspend();
        repository.save(digitalId);
        assertVerifyPass(new VerifyIdentityCommand(digitalId.getId(), VerificationScope.VALID));
    }

    @Test
    public void testRevokedIdentityFailValid() {
        DigitalId digitalId = createIdentity();
        digitalId.revoke();
        repository.save(digitalId);
        assertVerifyFail(new VerifyIdentityCommand(digitalId.getId(), VerificationScope.VALID));
    }

    @Test
    public void testUnknownIdentityFailPeriod() {
        assertVerifyFail(new VerifyIdentityBetweenCommand(UUID.randomUUID(), VerificationScope.ELIGIBLE, PERIOD_FROM, PERIOD_TO));
    }

    @Test
    public void testTooNewIdentityFailPeriod() {
        UUID id = createIdentity().getId();
        recordCreate(id, MID_PERIOD);
        assertVerifyFail(new VerifyIdentityBetweenCommand(id, VerificationScope.ELIGIBLE, PERIOD_FROM, PERIOD_TO));
    }

    @Test
    public void testActiveIdentityPassPeriod() {
        UUID id = createIdentity().getId();
        recordCreate(id, CREATED_AT);
        assertVerifyPass(new VerifyIdentityBetweenCommand(id, VerificationScope.ELIGIBLE, PERIOD_FROM, PERIOD_TO));
    }

    @Test
    public void testSuspendedIdentityFailPeriod() {
        UUID id = createIdentity().getId();
        recordCreate(id, CREATED_AT);
        recordStatusChange(id, BEFORE_PERIOD, DigitalIdStatus.SUSPENDED);
        assertVerifyFail( new VerifyIdentityBetweenCommand(id, VerificationScope.ELIGIBLE, PERIOD_FROM, PERIOD_TO));
    }

    @Test
    public void testDuringSuspendedIdentityFailPeriod() {
        UUID id = createIdentity().getId();
        recordCreate(id, CREATED_AT);
        recordStatusChange(id, MID_PERIOD, DigitalIdStatus.SUSPENDED);
        assertVerifyFail( new VerifyIdentityBetweenCommand(id, VerificationScope.ELIGIBLE, PERIOD_FROM, PERIOD_TO));
    }

    @Test
    public void testLaterSuspendedIdentityPassPeriod() {
        UUID id = createIdentity().getId();
        recordCreate(id, CREATED_AT);
        recordStatusChange(id, AFTER_PERIOD, DigitalIdStatus.SUSPENDED);
        assertVerifyPass(new VerifyIdentityBetweenCommand(id, VerificationScope.ELIGIBLE, PERIOD_FROM, PERIOD_TO));
    }

    private DigitalId createIdentity() {
        DigitalId digitalId = new DigitalId(UUID.randomUUID(), CREATED_AT, DATE_OF_BIRTH, "London", BiologicalSex.MALE, "John Doe", "123 Main St", DigitalIdStatus.ACTIVE);
        repository.save(digitalId);
        return digitalId;
    }

    private void recordCreate(UUID identityId, Instant at) {
        AuditAction action = new AuditAction.CreateIdentityAction(
            DATE_OF_BIRTH, "London", BiologicalSex.MALE, "John Doe", "123 Main St", DigitalIdStatus.ACTIVE
        );
        auditLog.record(new AuditEvent(UUID.randomUUID(), identityId, at, CALLER, action));
    }

    private void recordStatusChange(UUID identityId, Instant at, DigitalIdStatus status) {
        auditLog.record(new AuditEvent(UUID.randomUUID(), identityId, at, CALLER, new AuditAction.UpdateIdentityStatusAction(status)));
    }

    private void assertVerifyPass(VerifyIdentityCommand command) {
        assertTrue(verificationService.verifyIdentity(CALLER, command));
    }

    private void assertVerifyPass(VerifyIdentityBetweenCommand command) {
        assertTrue(verificationService.verifyIdentityBetween(CALLER, command));
    }

    private void assertVerifyFail(VerifyIdentityCommand command) {
        assertFalse(verificationService.verifyIdentity(CALLER, command));
    }

    private void assertVerifyFail(VerifyIdentityBetweenCommand command) {
        assertFalse(verificationService.verifyIdentityBetween(CALLER, command));
    }

}
