package com.github.ec25779.digitalid.verification;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.UUID;

public record VerifyIdentityBetweenCommand(@NotNull UUID id, @NotNull VerificationScope scope,
                                           @NotNull Instant from, @NotNull Instant to) {
}
