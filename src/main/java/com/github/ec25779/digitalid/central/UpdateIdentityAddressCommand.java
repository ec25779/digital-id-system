package com.github.ec25779.digitalid.central;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record UpdateIdentityAddressCommand(@NotNull UUID id, @NotNull String address) implements UpdateIdentityCommand {
}
