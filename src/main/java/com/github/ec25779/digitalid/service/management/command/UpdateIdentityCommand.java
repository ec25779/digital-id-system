package com.github.ec25779.digitalid.service.management.command;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public sealed interface UpdateIdentityCommand permits UpdateIdentityFullNameCommand, UpdateIdentityAddressCommand, UpdateIdentitySuspensionCommand {

    @NotNull UUID id();

}
