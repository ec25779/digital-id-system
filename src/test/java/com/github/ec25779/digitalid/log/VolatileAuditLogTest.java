package com.github.ec25779.digitalid.log;

import org.jetbrains.annotations.NotNull;

public class VolatileAuditLogTest extends AbstractAuditLogTest {

    @Override
    protected @NotNull AuditLog createAuditLog() {
        return new VolatileAuditLog();
    }

}
