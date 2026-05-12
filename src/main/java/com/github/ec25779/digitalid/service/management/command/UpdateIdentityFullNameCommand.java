package com.github.ec25779.digitalid.service.management.command;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record UpdateIdentityFullNameCommand(@NotNull UUID id, @NotNull String fullName) implements UpdateIdentityCommand {
}
