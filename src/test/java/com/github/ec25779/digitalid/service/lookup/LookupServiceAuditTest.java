package com.github.ec25779.digitalid.service.lookup;

import com.github.ec25779.digitalid.auth.OrganizationId;
import com.github.ec25779.digitalid.auth.OrganizationPermissionRegistry;
import com.github.ec25779.digitalid.auth.Permission;
import com.github.ec25779.digitalid.log.AuditAction;
import com.github.ec25779.digitalid.log.AuditLog;
import com.github.ec25779.digitalid.model.DigitalId;
import com.github.ec25779.digitalid.model.IdentityNotFoundException;
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
public class LookupServiceAuditTest {

    private static final OrganizationId CALLER = new OrganizationId("test");

    @Mock
    private LookupService delegate;

    @Mock
    private AuditLog auditLog;

    @Mock
    private DigitalId digitalId;

    private AuditingLookupService service;

    @BeforeEach
    void setUp() {
        service = new AuditingLookupService(delegate, auditLog);
    }

    @Test
    void testSuccessfulLookupRecordsAuditEvent() {
        UUID id = UUID.randomUUID();
        LookupIdentityCommand command = new LookupIdentityCommand(id);
        when(delegate.lookupIdentity(CALLER, command)).thenReturn(digitalId);
        when(digitalId.getId()).thenReturn(id);

        DigitalId result = service.lookupIdentity(CALLER, command);

        assertEquals(digitalId, result);
        verify(auditLog).record(id, CALLER, new AuditAction.LookupIdentityAction());
    }

    @Test
    void testFailedLookupDoesNotRecordAuditEvent() {
        UUID id = UUID.randomUUID();
        LookupIdentityCommand command = new LookupIdentityCommand(id);
        when(delegate.lookupIdentity(CALLER, command)).thenThrow(new IdentityNotFoundException(id));

        assertThrows(IdentityNotFoundException.class, () -> service.lookupIdentity(CALLER, command));
        verifyNoInteractions(auditLog);
    }

}
