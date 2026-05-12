package com.github.ec25779.digitalid.service.management.command;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record RevokeIdentityCommand(@NotNull UUID id) {
}
