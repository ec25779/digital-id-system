package com.github.ec25779.digitalid.auth;

import com.github.ec25779.digitalid.model.UnauthorizedOperationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OrganizationPermissionRegistryTest {

    private OrganizationPermissionRegistry registry;

    @BeforeEach
    public void setup() {
        registry = OrganizationPermissionRegistry.builder()
            .grant("org1", Permission.CREATE_IDENTITY)
            .build();
    }

    @Test
    public void testAuthorizedOrganizationHasPermission() {
        assertTrue(registry.hasPermission(new OrganizationId("org1"), Permission.CREATE_IDENTITY));
    }

    @Test
    public void testAuthorizedOrganizationRequiresSuccessful() {
        assertDoesNotThrow(() -> {
            registry.require(new OrganizationId("org1"), Permission.CREATE_IDENTITY);
        });
    }

    @Test
    public void testUnauthorizedOrganizationDoesNotHavePermission() {
        assertFalse(registry.hasPermission(new OrganizationId("org2"), Permission.CREATE_IDENTITY));
    }

    @Test
    public void testUnauthorizedOrganizationRequiresThrows() {
        assertThrows(UnauthorizedOperationException.class, () -> {
            registry.require(new OrganizationId("org2"), Permission.CREATE_IDENTITY);
        });
    }

}
