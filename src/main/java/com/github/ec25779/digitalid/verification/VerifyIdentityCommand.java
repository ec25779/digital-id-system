package com.github.ec25779.digitalid.verification;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record VerifyIdentityCommand(@NotNull UUID id, @NotNull VerificationScope scope) {
}
