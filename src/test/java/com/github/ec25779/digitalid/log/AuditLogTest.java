package com.github.ec25779.digitalid.log;

import com.github.ec25779.digitalid.auth.OrganizationId;
import com.github.ec25779.digitalid.model.DigitalIdStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuditLogTest {

    private static final UUID TEST_IDENTITY_ID = UUID.randomUUID();
    private static final Instant TEST_INSTANT = Instant.parse("2026-04-08T12:00:00Z");
    private static final Instant TEST_INTERVAL_START = Instant.parse("2026-04-08T11:00:00Z");
    private static final Instant TEST_INTERVAL_END = Instant.parse("2026-04-08T13:00:00Z");
    private static final Instant TEST_INSTANT_OUTSIDE_INTERVAL = Instant.parse("2026-04-08T14:00:00Z");

    private static final OrganizationId TEST_ORG = new OrganizationId("test-org");

    private AuditLog auditLog;

    @BeforeEach
    public void setUp() {
        auditLog = new VolatileAuditLog();
    }

    @Test
    public void testRecordEvent() {
        AuditAction action = new AuditAction.UpdateIdentityStatusAction(DigitalIdStatus.SUSPENDED);
        AuditEvent event = auditLog.record(TEST_IDENTITY_ID, TEST_ORG, action);

        Optional<AuditEvent> result = auditLog.getEvent(event.id());
        assertTrue(result.isPresent());
        assertEquals(event, result.get());
    }

    @Test
    public void testGetEvents() {
        AuditAction action1 = new AuditAction.UpdateIdentityStatusAction(DigitalIdStatus.SUSPENDED);
        AuditAction action2 = new AuditAction.UpdateIdentityStatusAction(DigitalIdStatus.ACTIVE);

        AuditEvent event1 = auditLog.record(TEST_IDENTITY_ID, TEST_ORG, action1);
        AuditEvent event2 = auditLog.record(TEST_IDENTITY_ID, TEST_ORG, action2);

        List<AuditEvent> events = auditLog.getEvents(TEST_IDENTITY_ID);
        assertEquals(2, events.size());
        assertEquals(event1, events.get(0));
        assertEquals(event2, events.get(1));
    }

    @Test
    public void testGetEventsBetween() {
        AuditAction action1 = new AuditAction.UpdateIdentityStatusAction(DigitalIdStatus.SUSPENDED);
        AuditEvent event1 = new AuditEvent(UUID.randomUUID(), TEST_IDENTITY_ID, TEST_INTERVAL_START, TEST_ORG, action1);

        AuditAction action2 = new AuditAction.UpdateIdentityStatusAction(DigitalIdStatus.ACTIVE);
        AuditEvent event2 = new AuditEvent(UUID.randomUUID(), TEST_IDENTITY_ID, TEST_INSTANT, TEST_ORG, action2);

        AuditAction action3 = new AuditAction.UpdateIdentityStatusAction(DigitalIdStatus.ACTIVE);
        AuditEvent event3 = new AuditEvent(UUID.randomUUID(), TEST_IDENTITY_ID, TEST_INSTANT_OUTSIDE_INTERVAL, TEST_ORG, action3);

        AuditAction action4 = new AuditAction.UpdateIdentityStatusAction(DigitalIdStatus.REVOKED);
        AuditEvent event4 = new AuditEvent(UUID.randomUUID(), TEST_IDENTITY_ID, TEST_INTERVAL_END, TEST_ORG, action4);

        auditLog.record(event1);
        auditLog.record(event2);
        auditLog.record(event3);
        auditLog.record(event4);

        List<AuditEvent> events = auditLog.getEventsBetween(TEST_IDENTITY_ID, TEST_INTERVAL_START, TEST_INTERVAL_END);
        assertEquals(3, events.size());
        assertEquals(event1, events.get(0));
        assertEquals(event2, events.get(1));
        assertEquals(event4, events.get(2));
    }

}
