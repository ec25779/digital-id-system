package com.github.ec25779.digitalid.service.lookup.command;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record LookupIdentityCommand(@NotNull UUID id) {
}
