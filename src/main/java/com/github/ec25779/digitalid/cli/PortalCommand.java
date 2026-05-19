package com.github.ec25779.digitalid.cli;

import org.jetbrains.annotations.NotNull;

public record PortalCommand(@NotNull String name, @NotNull String description, @NotNull Runnable action) {
}
