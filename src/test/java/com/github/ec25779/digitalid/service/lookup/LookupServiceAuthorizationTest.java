package com.github.ec25779.digitalid.service.lookup;

import com.github.ec25779.digitalid.auth.OrganizationId;
import com.github.ec25779.digitalid.auth.OrganizationPermissionRegistry;
import com.github.ec25779.digitalid.auth.Permission;
import com.github.ec25779.digitalid.model.DigitalId;
import com.github.ec25779.digitalid.model.UnauthorizedOperationException;
import com.github.ec25779.digitalid.service.lookup.command.LookupIdentityCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LookupServiceAuthorizationTest {

    private static final OrganizationId ALLOWED_CALLER = new OrganizationId("test-allowed");
    private static final OrganizationId DISALLOWED_CALLER = new OrganizationId("test-disallowed");

    @Mock
    private LookupService delegate;

    private AuthorizingLookupService service;

    @BeforeEach
    void setUp() {
        OrganizationPermissionRegistry permissionRegistry = OrganizationPermissionRegistry.builder()
            .grant(ALLOWED_CALLER, Permission.LOOKUP_IDENTITY)
            .build();

        service = new AuthorizingLookupService(delegate, permissionRegistry);
    }

    @Test
    void testAuthorizedCallerDelegates() {
        LookupIdentityCommand command = new LookupIdentityCommand(UUID.randomUUID());
        DigitalId expected = mock(DigitalId.class);
        when(delegate.lookupIdentity(eq(ALLOWED_CALLER), any())).thenReturn(expected);

        DigitalId actual = service.lookupIdentity(ALLOWED_CALLER, command);

        assertEquals(expected, actual);
        verify(delegate).lookupIdentity(ALLOWED_CALLER, command);
    }

    @Test
    void testUnauthorizedCallerThrowsAndDoesNotDelegate() {
        LookupIdentityCommand command = new LookupIdentityCommand(UUID.randomUUID());

        assertThrows(UnauthorizedOperationException.class, () -> service.lookupIdentity(DISALLOWED_CALLER, command));
        verifyNoInteractions(delegate);
    }

}
