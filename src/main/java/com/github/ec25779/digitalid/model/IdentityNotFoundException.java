package com.github.ec25779.digitalid.model;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class IdentityNotFoundException extends IllegalStateException {

    public IdentityNotFoundException(@NotNull UUID id) {
        super("Identity with id " + id + " not found");
    }

}
