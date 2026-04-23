package com.github.ec25779.digitalid.central;

import com.github.ec25779.digitalid.auth.OrganizationId;
import com.github.ec25779.digitalid.auth.OrganizationPermissionRegistry;
import com.github.ec25779.digitalid.auth.Permission;
import com.github.ec25779.digitalid.model.BiologicalSex;
import com.github.ec25779.digitalid.model.DigitalId;
import com.github.ec25779.digitalid.repository.DigitalIdRepository;
import com.github.ec25779.digitalid.repository.VolatileDigitalIdRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CentralAuthorityTest {

    private static final LocalDate TEST_DATE_OF_BIRTH = LocalDate.of(2000, 1, 1);

    @Test
    public void testCreateIdentity() {
        DigitalIdRepository repository = new VolatileDigitalIdRepository();
        OrganizationPermissionRegistry permissionRegistry = OrganizationPermissionRegistry.builder()
            .grant("central-authority", Permission.CREATE_IDENTITY)
            .build();

        DigitalIdentityManager identityManager = new AuthorizingIdentityManager(
            new CoreIdentityManager(repository), permissionRegistry
        );

        DigitalId result = identityManager.createIdentity(
            new OrganizationId("central-authority"),
            new CreateIdentityCommand(TEST_DATE_OF_BIRTH, "London", BiologicalSex.MALE, "John Doe", "123 Main St")
        );

        Optional<DigitalId> digitalId = repository.find(result.getId());
        assertTrue(digitalId.isPresent());
        assertEquals(digitalId.get(), result);
    }

}
