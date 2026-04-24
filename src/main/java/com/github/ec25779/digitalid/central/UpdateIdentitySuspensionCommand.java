package com.github.ec25779.digitalid.central;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record UpdateIdentitySuspensionCommand(@NotNull UUID id, boolean suspended) implements UpdateIdentityCommand {
}
