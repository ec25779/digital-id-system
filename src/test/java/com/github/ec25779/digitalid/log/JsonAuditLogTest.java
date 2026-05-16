package com.github.ec25779.digitalid.log;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonAuditLogTest extends AbstractAuditLogTest {

    @TempDir
    private Path tempDir;

    @Override
    protected @NotNull AuditLog createAuditLog() {
        return new JsonAuditLog(tempDir.resolve("auditLog.json").toFile());
    }

    @Test
    public void testAuditLogPersists() {
        AuditEvent event = auditLog.record(TEST_IDENTITY_ID, TEST_ORG, new AuditAction.LookupIdentityAction());

        auditLog = createAuditLog();
        Optional<AuditEvent> result = auditLog.getEvent(event.id());

        assertTrue(result.isPresent());
        assertEquals(event, result.get());
    }

}
