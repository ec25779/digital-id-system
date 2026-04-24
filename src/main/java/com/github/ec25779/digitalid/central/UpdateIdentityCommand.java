package com.github.ec25779.digitalid.central;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public sealed interface UpdateIdentityCommand permits UpdateIdentityFullNameCommand, UpdateIdentityAddressCommand, UpdateIdentitySuspensionCommand {

    @NotNull UUID id();

}
