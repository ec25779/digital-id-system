package com.github.ec25779.digitalid.service.management;

import com.github.ec25779.digitalid.auth.OrganizationId;
import com.github.ec25779.digitalid.auth.OrganizationPermissionRegistry;
import com.github.ec25779.digitalid.auth.Permission;
import com.github.ec25779.digitalid.log.AuditAction;
import com.github.ec25779.digitalid.log.AuditEvent;
import com.github.ec25779.digitalid.log.AuditLog;
import com.github.ec25779.digitalid.log.VolatileAuditLog;
import com.github.ec25779.digitalid.model.BiologicalSex;
import com.github.ec25779.digitalid.model.DigitalId;
import com.github.ec25779.digitalid.model.DigitalIdStatus;
import com.github.ec25779.digitalid.model.InvalidStateTransitionException;
import com.github.ec25779.digitalid.repository.DigitalIdRepository;
import com.github.ec25779.digitalid.repository.VolatileDigitalIdRepository;
import com.github.ec25779.digitalid.service.management.command.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ManagementServiceTest {

    private static final OrganizationId ORGANIZATION_ID = new OrganizationId("central-authority");
    private static final Instant FIXED_NOW = Instant.parse("2026-04-23T00:00:00Z");
    private static final LocalDate TEST_DATE_OF_BIRTH = LocalDate.of(2000, 1, 1);

    private DigitalIdRepository repository;
    private ManagementService identityManager;
    private AuditLog auditLog;

    @BeforeEach
    public void setUp() {
        Clock clock = Clock.fixed(FIXED_NOW, ZoneOffset.UTC);

        repository = new VolatileDigitalIdRepository();
        auditLog = new VolatileAuditLog(clock);

        OrganizationPermissionRegistry permissionRegistry = OrganizationPermissionRegistry.builder()
            .grant(ORGANIZATION_ID, Permission.CREATE_IDENTITY, Permission.UPDATE_IDENTITY, Permission.REVOKE_IDENTITY)
            .build();

        identityManager = new AuthorizingManagementService(new AuditingManagementService(
            new ManagementServiceImpl(repository, clock), auditLog
        ), permissionRegistry);
    }

    @Test
    public void testCreateIdentity() {
        DigitalId result = identityManager.createIdentity(ORGANIZATION_ID,
            new CreateIdentityCommand(TEST_DATE_OF_BIRTH, "London", BiologicalSex.MALE, "John Doe", "123 Main St")
        );

        Optional<DigitalId> digitalId = repository.find(result.getId());
        assertTrue(digitalId.isPresent());
        assertEquals(digitalId.get(), result);
        assertEquals(FIXED_NOW, result.getCreatedAt());

        List<AuditEvent> events = auditLog.getEvents(result.getId());
        assertEquals(1, events.size());
        assertInstanceOf(AuditAction.CreateIdentityAction.class, events.getFirst().action());
    }

    @Test
    public void testUpdateIdentityFullName() {
        DigitalId createResult = identityManager.createIdentity(ORGANIZATION_ID,
            new CreateIdentityCommand(TEST_DATE_OF_BIRTH, "London", BiologicalSex.MALE, "John Doe", "123 Main St")
        );

        UUID id = createResult.getId();

        DigitalId updateResult = identityManager.updateIdentity(ORGANIZATION_ID,
            new UpdateIdentityFullNameCommand(id, "Jane Doe")
        );

        assertEquals(id, updateResult.getId());
        assertEquals("Jane Doe", updateResult.getFullName());

        DigitalId findResult = repository.find(id).orElseThrow();
        assertEquals(id, findResult.getId());
        assertEquals("Jane Doe", updateResult.getFullName());

        List<AuditEvent> events = auditLog.getEvents(id);
        assertEquals(2, events.size());
        assertInstanceOf(AuditAction.UpdateIdentityFullNameAction.class, events.getLast().action());
    }

    @Test
    public void testUpdateIdentityAddress() {
        DigitalId createResult = identityManager.createIdentity(ORGANIZATION_ID,
            new CreateIdentityCommand(TEST_DATE_OF_BIRTH, "London", BiologicalSex.MALE, "John Doe", "123 Main St")
        );

        UUID id = createResult.getId();

        DigitalId updateResult = identityManager.updateIdentity(ORGANIZATION_ID,
            new UpdateIdentityAddressCommand(id, "456 Elm St")
        );

        assertEquals(id, updateResult.getId());
        assertEquals("456 Elm St", updateResult.getAddress());

        DigitalId findResult = repository.find(id).orElseThrow();
        assertEquals(id, findResult.getId());
        assertEquals("456 Elm St", updateResult.getAddress());

        List<AuditEvent> events = auditLog.getEvents(id);
        assertEquals(2, events.size());
        assertInstanceOf(AuditAction.UpdateIdentityAddressAction.class, events.getLast().action());
    }

    @Test
    public void testSuspendActiveIdentity() {
        DigitalId createResult = identityManager.createIdentity(ORGANIZATION_ID,
            new CreateIdentityCommand(TEST_DATE_OF_BIRTH, "London", BiologicalSex.MALE, "John Doe", "123 Main St")
        );

        UUID id = createResult.getId();

        DigitalId suspendResult = identityManager.updateIdentity(ORGANIZATION_ID,
            new UpdateIdentitySuspensionCommand(id, true)
        );

        assertEquals(id, suspendResult.getId());
        assertEquals(DigitalIdStatus.SUSPENDED, suspendResult.getStatus());

        List<AuditEvent> events = auditLog.getEvents(id);
        assertEquals(2, events.size());
        assertInstanceOf(AuditAction.UpdateIdentityStatusAction.class, events.getLast().action());
    }

    @Test
    public void testAlreadySuspendedIdentityThrows() {
        DigitalId createResult = identityManager.createIdentity(ORGANIZATION_ID,
            new CreateIdentityCommand(TEST_DATE_OF_BIRTH, "London", BiologicalSex.MALE, "John Doe", "123 Main St")
        );

        UUID id = createResult.getId();

        identityManager.updateIdentity(ORGANIZATION_ID,
            new UpdateIdentitySuspensionCommand(id, true)
        );

        assertThrows(InvalidStateTransitionException.class, () -> {
            identityManager.updateIdentity(ORGANIZATION_ID,
                new UpdateIdentitySuspensionCommand(id, true)
            );
        });

        List<AuditEvent> events = auditLog.getEvents(id);
        assertEquals(2, events.size());
        assertInstanceOf(AuditAction.UpdateIdentityStatusAction.class, events.getLast().action());
    }

    @Test
    public void testActivateSuspendedIdentity() {
        DigitalId createResult = identityManager.createIdentity(ORGANIZATION_ID,
            new CreateIdentityCommand(TEST_DATE_OF_BIRTH, "London", BiologicalSex.MALE, "John Doe", "123 Main St")
        );

        UUID id = createResult.getId();

        identityManager.updateIdentity(ORGANIZATION_ID,
            new UpdateIdentitySuspensionCommand(id, true)
        );

        DigitalId activatedResult = identityManager.updateIdentity(ORGANIZATION_ID,
            new UpdateIdentitySuspensionCommand(id, false)
        );

        assertEquals(id, activatedResult.getId());
        assertEquals(DigitalIdStatus.ACTIVE, activatedResult.getStatus());

        List<AuditEvent> events = auditLog.getEvents(id);
        assertEquals(3, events.size());
        assertInstanceOf(AuditAction.UpdateIdentityStatusAction.class, events.get(1).action());
        assertEquals(DigitalIdStatus.SUSPENDED, ((AuditAction.UpdateIdentityStatusAction) events.get(1).action()).status());
        assertInstanceOf(AuditAction.UpdateIdentityStatusAction.class, events.get(2).action());
        assertEquals(DigitalIdStatus.ACTIVE, ((AuditAction.UpdateIdentityStatusAction) events.get(2).action()).status());
    }

    @Test
    public void testRevokeActiveIdentity() {
        DigitalId createResult = identityManager.createIdentity(ORGANIZATION_ID,
            new CreateIdentityCommand(TEST_DATE_OF_BIRTH, "London", BiologicalSex.MALE, "John Doe", "123 Main St")
        );

        UUID id = createResult.getId();
        DigitalId revokedResult = identityManager.revokeIdentity(ORGANIZATION_ID, new RevokeIdentityCommand(id));

        assertEquals(id, revokedResult.getId());
        assertEquals(DigitalIdStatus.REVOKED, revokedResult.getStatus());

        List<AuditEvent> events = auditLog.getEvents(id);
        assertEquals(2, events.size());
        assertInstanceOf(AuditAction.UpdateIdentityStatusAction.class, events.getLast().action());
        assertEquals(DigitalIdStatus.REVOKED, ((AuditAction.UpdateIdentityStatusAction) events.getLast().action()).status());
    }

    @Test
    public void testUpdateRevokedIdentityThrows() {
        UUID id = identityManager.createIdentity(ORGANIZATION_ID,
            new CreateIdentityCommand(TEST_DATE_OF_BIRTH, "London", BiologicalSex.MALE, "John Doe", "123 Main St")
        ).getId();

        DigitalId digitalId = identityManager.revokeIdentity(ORGANIZATION_ID,
            new RevokeIdentityCommand(id)
        );

        assertThrows(InvalidStateTransitionException.class, () -> {
            identityManager.updateIdentity(ORGANIZATION_ID, new UpdateIdentityFullNameCommand(id, "Jane Doe"));
        });

        assertThrows(InvalidStateTransitionException.class, () -> {
            identityManager.updateIdentity(ORGANIZATION_ID, new UpdateIdentityAddressCommand(id, "456 Elm St"));
        });

        assertThrows(InvalidStateTransitionException.class, () -> {
            identityManager.updateIdentity(ORGANIZATION_ID, new UpdateIdentitySuspensionCommand(id, true));
        });

        assertThrows(InvalidStateTransitionException.class, () -> {
            identityManager.updateIdentity(ORGANIZATION_ID, new UpdateIdentitySuspensionCommand(id, false));
        });

        assertEquals(DigitalIdStatus.REVOKED, digitalId.getStatus());
        assertEquals("John Doe", digitalId.getFullName());
        assertEquals("123 Main St", digitalId.getAddress());

        List<AuditEvent> events = auditLog.getEvents(id);
        assertEquals(2, events.size());
        assertInstanceOf(AuditAction.UpdateIdentityStatusAction.class, events.getLast().action());
        assertEquals(DigitalIdStatus.REVOKED, ((AuditAction.UpdateIdentityStatusAction) events.getLast().action()).status());
    }

    @AfterEach
    public void tearDown() {
        repository = null;
        identityManager = null;
    }

}
