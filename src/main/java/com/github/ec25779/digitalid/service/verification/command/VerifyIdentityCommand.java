package com.github.ec25779.digitalid.service.verification.command;

import com.github.ec25779.digitalid.service.verification.VerificationScope;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record VerifyIdentityCommand(@NotNull UUID id, @NotNull VerificationScope scope) {
}
