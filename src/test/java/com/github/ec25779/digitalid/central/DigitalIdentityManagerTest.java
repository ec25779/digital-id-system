package com.github.ec25779.digitalid.central;

import com.github.ec25779.digitalid.auth.OrganizationId;
import com.github.ec25779.digitalid.auth.OrganizationPermissionRegistry;
import com.github.ec25779.digitalid.auth.Permission;
import com.github.ec25779.digitalid.model.BiologicalSex;
import com.github.ec25779.digitalid.model.DigitalId;
import com.github.ec25779.digitalid.model.DigitalIdStatus;
import com.github.ec25779.digitalid.model.InvalidStateTransitionException;
import com.github.ec25779.digitalid.repository.DigitalIdRepository;
import com.github.ec25779.digitalid.repository.VolatileDigitalIdRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class DigitalIdentityManagerTest {

    private static final OrganizationId ORGANIZATION_ID = new OrganizationId("central-authority");
    private static final Instant FIXED_NOW = Instant.parse("2026-04-23T00:00:00Z");
    private static final LocalDate TEST_DATE_OF_BIRTH = LocalDate.of(2000, 1, 1);

    private DigitalIdRepository repository;
    private DigitalIdentityManager identityManager;

    @BeforeEach
    public void setUp() {
        repository = new VolatileDigitalIdRepository();
        OrganizationPermissionRegistry permissionRegistry = OrganizationPermissionRegistry.builder()
            .grant(ORGANIZATION_ID, Permission.CREATE_IDENTITY, Permission.UPDATE_IDENTITY)
            .build();

        Clock clock = Clock.fixed(FIXED_NOW, ZoneOffset.UTC);
        identityManager = new AuthorizingIdentityManager(
            new CoreIdentityManager(repository, clock), permissionRegistry
        );
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
    }

    @AfterEach
    public void tearDown() {
        repository = null;
        identityManager = null;
    }

}
